// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers.QueryBuilder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class Alarms implements IAlarms {
    private final IStorageClient storageClient;
    private String databaseName;
    private String collectionId;

    @Inject
    public Alarms(IServicesConfig servicesConfig, IStorageClient storageClient) throws Exception {
        this.storageClient = storageClient;
        this.databaseName = servicesConfig.getAlarmsStorageConfig().getDocumentDbDatabase();
        this.collectionId = servicesConfig.getAlarmsStorageConfig().getDocumentDbCollection();
    }

    @Override
    public AlarmServiceModel get(String id) throws Exception {
        return new AlarmServiceModel(this.getDocumentById(id));
    }

    @Override
    public ArrayList<AlarmServiceModel> getListByRule(String id, DateTime from, DateTime to, String order, int skip,
                                                      int limit, String[] devices) throws Exception {
        String sqlQuery = QueryBuilder.buildSQL(
                "alarm",
                id, "rule.id",
                from, "created",
                to, "created",
                order, "created",
                skip,
                limit,
                devices, "deviceId");
        ArrayList<Document> docs = this.storageClient.queryDocuments(
                this.databaseName,
                this.collectionId,
                null,
                sqlQuery,
                skip);

        ArrayList<AlarmServiceModel> alarms = new ArrayList<AlarmServiceModel>();
        for (Document doc : docs) {
            alarms.add(new AlarmServiceModel(doc));
        }

        return alarms;
    }

    @Override
    public ArrayList<AlarmServiceModel> getList(DateTime from, DateTime to, String order, int skip,
                                             int limit, String[] devices) throws Exception {
        String sqlQuery = QueryBuilder.buildSQL(
                "alarm",
                null, null,
                from, "created",
                to, "created",
                order, "created",
                skip,
                limit,
                devices, "deviceId");
        ArrayList<Document> docs = this.storageClient.queryDocuments(
                this.databaseName,
                this.collectionId,
                null,
                sqlQuery,
                skip);

        ArrayList<AlarmServiceModel> alarms = new ArrayList<AlarmServiceModel>();
        for (Document doc : docs) {
            alarms.add(new AlarmServiceModel(doc));
        }

        return alarms;
    }

    public AlarmServiceModel update(String id, String status) throws Exception {
        Document document = getDocumentById(id);
        document.set("status", status);

        document = this.storageClient.upsertDocument(
                this.databaseName,
                this.collectionId,
                document
        );

        return new AlarmServiceModel(document);
    }

    private Document getDocumentById(String id) throws Exception {
        // Retrieve the document using the DocumentClient.
        ArrayList<Document> documentList = this.storageClient.queryDocuments(
                this.databaseName,
                this.collectionId,
                null,
                "SELECT * FROM c WHERE c.id='" + id + "'",
                0);

        if (documentList.size() > 0) {
            return documentList.get(0);
        } else {
            return null;
        }
    }
}
