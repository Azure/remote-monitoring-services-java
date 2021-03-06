// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers.QueryBuilder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.cosmosDb.IStorageClient;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import play.Logger;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class Alarms implements IAlarms {

    private final IStorageClient storageClient;
    private String databaseName;
    private String collectionId;
    private final int maxDeleteRetryCount;

    private static final Logger.ALogger log = Logger.of(Alarms.class);

    // constants for storage keys
    private static final String MESSAGE_RECEIVED_KEY = "device.msg.received";
    private static final String RULE_ID_KEY = "rule.id";
    private static final String DEVICE_ID_KEY = "device.id";
    private static final String STATUS_KEY = "status";
    private static final String ALARM_SCHEMA_KEY = "alarm";
    private static final String AGGREGATE_COUNT_KEY = "_aggregate";

    private static final String ALARM_STATUS_OPEN = "open";
    private static final String ALARM_STATUS_ACKNOWLEDGED = "acknowledged";

    @Inject
    public Alarms(IServicesConfig servicesConfig, IStorageClient storageClient) {
        this.storageClient = storageClient;
        this.databaseName = servicesConfig.getAlarmsConfig().getStorageConfig().getCosmosDbDatabase();
        this.collectionId = servicesConfig.getAlarmsConfig().getStorageConfig().getCosmosDbCollection();
        this.maxDeleteRetryCount = servicesConfig.getAlarmsConfig().getMaxDeleteRetries();
    }

    @Override
    public AlarmServiceModel get(String id) throws Exception {
        return new AlarmServiceModel(this.getDocumentById(id));
    }

    @Override
    public ArrayList<AlarmServiceModel> getListByRuleId(String id, DateTime from, DateTime to, String order, int skip,
                                                        int limit, String[] devices) throws Exception {
        SqlQuerySpec querySpec = QueryBuilder.getDocumentsSQL(
            ALARM_SCHEMA_KEY,
            id, RULE_ID_KEY,
            from, MESSAGE_RECEIVED_KEY,
            to, MESSAGE_RECEIVED_KEY,
            order, MESSAGE_RECEIVED_KEY,
            skip,
            limit,
            devices, DEVICE_ID_KEY);
        ArrayList<Document> docs = this.storageClient.queryDocuments(
            this.databaseName,
            this.collectionId,
            null,
            querySpec,
            skip);

        ArrayList<AlarmServiceModel> alarms = new ArrayList<>();
        for (Document doc : docs) {
            alarms.add(new AlarmServiceModel(doc));
        }

        return alarms;
    }

    /*
     * Returns the count of alarms for a given rule id. Can be filtered by time
     * period (from/to), and devices.
     */
    @Override
    public int getCountByRuleId(
        String ruleId,
        DateTime from,
        DateTime to,
        String[] devices) throws Exception {

        // build sql query to get open/acknowledged alarm count for rule
        String[] statusList = {ALARM_STATUS_OPEN, ALARM_STATUS_ACKNOWLEDGED};
        SqlQuerySpec querySpec = QueryBuilder.getCountSQL(
            ALARM_SCHEMA_KEY,
            ruleId, RULE_ID_KEY,
            from, MESSAGE_RECEIVED_KEY,
            to, MESSAGE_RECEIVED_KEY,
            devices, DEVICE_ID_KEY,
            statusList, STATUS_KEY);

        Document doc;

        try {
            // set query options
            FeedOptions queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setEnableScanInQuery(true);

            // request count for ruleId with given parameters
            ArrayList<Document> resultList = this.storageClient.queryDocuments(
                this.databaseName,
                this.collectionId,
                queryOptions,
                querySpec,
                0);
            if (resultList.size() > 0) {
                doc = resultList.get(0);
            } else {
                // There are no alarms for the request parameters
                return 0;
            }
        } catch (java.lang.Exception e) {
            log.error("Could not retrieve alarm count for rule id "
                + ruleId, e);
            throw new CompletionException(
                new ExternalDependencyException(
                    "Could not retrieve alarm count for rule id "
                        + ruleId, e));
        }

        return doc.getInt(AGGREGATE_COUNT_KEY);
    }

    @Override
    public ArrayList<AlarmServiceModel> getList(DateTime from, DateTime to, String order, int skip,
                                                int limit, String[] devices) throws Exception {
        SqlQuerySpec querySpec = QueryBuilder.getDocumentsSQL(
            ALARM_SCHEMA_KEY,
            null, null,
            from, MESSAGE_RECEIVED_KEY,
            to, MESSAGE_RECEIVED_KEY,
            order, MESSAGE_RECEIVED_KEY,
            skip,
            limit,
            devices, DEVICE_ID_KEY);
        ArrayList<Document> docs = this.storageClient.queryDocuments(
            this.databaseName,
            this.collectionId,
            null,
            querySpec,
            skip);

        ArrayList<AlarmServiceModel> alarms = new ArrayList<>();
        for (Document doc : docs) {
            alarms.add(new AlarmServiceModel(doc));
        }

        return alarms;
    }

    public AlarmServiceModel update(String id, String status) throws Exception {
        Document document = getDocumentById(id);
        document.set(STATUS_KEY, status);

        document = this.storageClient.upsertDocument(
            this.databaseName,
            this.collectionId,
            document
        );

        return new AlarmServiceModel(document);
    }

    /**
     * Delete list of alarms by id
     * @param ids
     * @throws Throwable
     */
    @Override
    public void delete(ArrayList<String> ids) throws Throwable {
        CompletableFuture[] tasks = new CompletableFuture[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            tasks[i] = CompletableFuture.runAsync(() -> {
                try {
                    this.delete(id);
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            });
        }

        CompletableFuture result = CompletableFuture.allOf(tasks);
        try {
            result.get();
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Delete single alarm by id
     * @param id
     * @throws Exception
     */
    @Override
    public void delete(String id) throws Exception {
        int retryCount = 0;
        while (retryCount < this.maxDeleteRetryCount) {
            try {
                this.storageClient.deleteDocument(this.databaseName, this.collectionId, id);
                return;
            } catch (Exception e) {
                long timeout = 0;
                if (e instanceof DocumentClientException)
                {
                    DocumentClientException clientException = (DocumentClientException)e;
                    if (clientException.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                        return;
                    }
                    timeout = clientException.getRetryAfterInMilliseconds();
                }

                retryCount++;
                if (retryCount >= this.maxDeleteRetryCount) {
                    this.log.error("Failed to delete alarm " + id, e);
                    throw new ExternalDependencyException(e.getMessage(), e);
                }

                this.log.warn("Exception on delete alarm " + id + ", retrying", e);
                Thread.sleep(timeout);
            }
        }
    }

    private Document getDocumentById(String id) throws Exception {
        // Retrieve the document using the DocumentClient.
        SqlParameterCollection parameterCollection = new SqlParameterCollection(new SqlParameter("@id", id));
        SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.id=@id", parameterCollection);
        ArrayList<Document> documentList = this.storageClient.queryDocuments(
            this.databaseName,
            this.collectionId,
            null,
            querySpec,
            0);

        if (documentList.size() > 0) {
            return documentList.get(0);
        } else {
            return null;
        }
    }
}
