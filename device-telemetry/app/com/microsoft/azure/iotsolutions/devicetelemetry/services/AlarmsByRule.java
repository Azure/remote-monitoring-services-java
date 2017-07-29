// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.FeedResponse;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers.QueryBuilder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;

public class AlarmsByRule implements IAlarmsByRule {
    private final IStorageClient storageClient;
    private String collectionId = "alarms";

    @Inject
    public AlarmsByRule(IStorageClient storageClient, String collectionId) throws Exception {
        this.storageClient = storageClient;
        if(collectionId != null && !collectionId.isEmpty()) {
            this.collectionId = collectionId;
        }
        this.storageClient.createCollectionIfNotExists(this.collectionId);
    }

    @Override
    public AlarmServiceModel get(String id, DateTime from, DateTime to, String order, int skip,
                                 int limit, String[] devices) throws Exception {
        String sqlQuery = QueryBuilder.buildSQL(
            "alarm",
            id,"rule.id",
            from,"created",
            to,"created",
            order,"created",
            skip,
            limit,
            devices,"deviceId");
        FeedResponse<Document> response = this.storageClient.queryDocuments(
            this.collectionId,
            null,
            sqlQuery);

        Iterator<Document> iterator = response.getQueryIterator();
        while(iterator.hasNext()) {
            Document doc = iterator.next();
            return new AlarmServiceModel(doc);
        }

        return null;
    }

    @Override
    public ArrayList<AlarmServiceModel> getList(DateTime from, DateTime to, String order, int skip,
                                                int limit, String[] devices) throws Exception {
        String sqlQuery = QueryBuilder.buildSQL(
            "alarm",
            null,null,
            from,"created",
            to,"created",
            order,"created",
            skip,
            limit,
            devices,"deviceId");
        FeedResponse<Document> response = this.storageClient.queryDocuments
            (this.collectionId,
                null,
                sqlQuery);
        Iterator<Document> iterator = response.getQueryIterator();
        ArrayList<AlarmServiceModel> alarms = new ArrayList<AlarmServiceModel>();
        while(iterator.hasNext()) {
            Document doc = iterator.next();
            alarms.add(new AlarmServiceModel(doc));
        }

        return alarms;
    }
}
