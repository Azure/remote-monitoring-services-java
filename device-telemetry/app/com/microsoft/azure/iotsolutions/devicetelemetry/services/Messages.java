// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers.QueryBuilder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import org.joda.time.DateTime;
import play.Logger;

import java.util.ArrayList;
import java.util.HashSet;

// TODO: use StorageClient
public final class Messages implements IMessages {

    private static final Logger.ALogger log = Logger.of(Messages.class);

    private final String dataPrefix = "data.";

    private final DocumentClient docDbConnection;
    private final String docDbCollectionLink;

    @Inject
    public Messages(
        IServicesConfig servicesConfig) {

        this.docDbCollectionLink = String.format(
            "/dbs/%s/colls/%s",
            servicesConfig.getMessagesStorageConfig().getDocumentDbDatabase(),
            servicesConfig.getMessagesStorageConfig().getDocumentDbCollection());

        this.docDbConnection = new DocumentClient(
            servicesConfig.getMessagesStorageConfig().getDocumentDbUri(),
            servicesConfig.getMessagesStorageConfig().getDocumentDbKey(),
            ConnectionPolicy.GetDefault(),
            ConsistencyLevel.Eventual);
    }

    public MessageListServiceModel getList(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] devices) {

        int dataPrefixLen = dataPrefix.length();

        String sql = QueryBuilder.getDocumentsSQL(
            "d2cmessage",
            null, null,
            from, "device.msg.received",
            to, "device.msg.received",
            order, "device.msg.received",
            skip,
            limit,
            devices, "device.id");
        ArrayList<Document> docs = query(sql, skip);

        // Messages to return
        ArrayList<MessageServiceModel> messages = new ArrayList<>();

        // Auto discovered telemetry types
        HashSet<String> properties = new HashSet<>();

        for (Document doc : docs) {

            // Document fields to expose
            ObjectNode data = new ObjectMapper().createObjectNode();

            // Extract all the telemetry data and types
            doc.getHashMap().entrySet().stream()
                // Ignore fields that don't start with "data."
                .filter(x -> x.getKey().startsWith(dataPrefix))
                .forEach(x -> {
                    // Remove the "data." prefix
                    String key = x.getKey().substring(dataPrefixLen);
                    data.putPOJO(key, x.getValue());

                    // Telemetry types auto-discovery magic
                    properties.add(key);
                });

            messages.add(new MessageServiceModel(
                doc.getString("device.id"),
                doc.getLong("device.msg.received"),
                data));
        }

        return new MessageListServiceModel(messages, new ArrayList<>(properties));
    }

    private ArrayList<Document> query(String sql, int skip) {

        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setEnableCrossPartitionQuery(true);
        queryOptions.setEnableScanInQuery(true);

        ArrayList<Document> docs = new ArrayList<>();
        String continuationToken = null;
        do {
            FeedResponse<Document> queryResults = this.docDbConnection.queryDocuments(
                this.docDbCollectionLink,
                sql,
                queryOptions);

            for (Document doc : queryResults.getQueryIterable()) {
                if (skip == 0) {
                    docs.add(doc);
                } else {
                    skip--;
                }
            }

            continuationToken = queryResults.getResponseContinuation();
            queryOptions.setRequestContinuation(continuationToken);
        } while (continuationToken != null);

        return docs;
    }
}
