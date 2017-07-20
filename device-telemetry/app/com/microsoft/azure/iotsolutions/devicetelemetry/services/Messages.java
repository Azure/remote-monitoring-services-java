// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;

import java.util.ArrayList;
import java.util.Hashtable;

public final class Messages implements IMessages {

    @Inject
    public Messages() {
    }

    public ArrayList<MessageServiceModel> getList() {
        return this.getSampleMessages();
    }

    /**
     * Get sample messages to return to client.
     * TODO: remove after storage dependency is added
     *
     * @return sample messages array
     */
    private ArrayList<MessageServiceModel> getSampleMessages() {
        ArrayList<MessageServiceModel> sampleMessages = new ArrayList<>();
        Object sampleMessage = null;

        // sample message 1
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "74");
            put("t_unit", "F");
            put("humidity", "41");
            put("latitude", "47.642272");
            put("longitude", "-122.103374");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Weather1",
            "2017-01-13T21:39:45-08:00",
            sampleMessage));

        // sample message 2
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "53");
            put("t_unit", "F");
            put("humidity", "51");
            put("latitude", "47.633281");
            put("longitude", "-122.112881");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Weather2",
            "2017-01-14T03:13:04-08:00",
            sampleMessage));

        // sample message 3
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "82");
            put("t_unit", "F");
            put("humidity", "76");
            put("latitude", "47.601010");
            put("longitude", "-122.164454");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Weather3",
            "2017-01-15T11:04:25-08:00",
            sampleMessage));

        // sample message 4
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "82");
            put("t_unit", "F");
            put("ph", "7.2");
            put("uv", "60");
            put("orp", "650");
            put("latitude", "47.728617");
            put("longitude", "-122.121120");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Pool1",
            "2017-01-15T11:04:30-08:00",
            sampleMessage));

        // sample message 5
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "81");
            put("t_unit", "F");
            put("ph", "7.1");
            put("uv", "54");
            put("orp", "652");
            put("latitude", "47.728617");
            put("longitude", "-122.121120");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Pool1",
            "2017-01-15T11:05:32-08:00",
            sampleMessage));

        // sample message 6
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "83");
            put("t_unit", "F");
            put("ph", "7.4");
            put("uv", "54");
            put("orp", "642");
            put("latitude", "47.728617");
            put("longitude", "-122.121120");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Pool1",
            "2017-01-15T11:06:34-08:00",
            sampleMessage));

        // sample message 7
        sampleMessage = new Hashtable<String, String>() {{
            put("temperature", "80");
            put("t_unit", "F");
            put("ph", "7.0");
            put("uv", "52");
            put("orp", "646");
            put("latitude", "47.728617");
            put("longitude", "-122.121120");
        }};

        sampleMessages.add(new MessageServiceModel(
            "Pool1",
            "2017-01-15T11:07:35-08:00",
            sampleMessage));

        return sampleMessages;
    }
}
