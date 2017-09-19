// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class MessageListApiModel {

    private final ArrayList<MessageApiModel> items = new ArrayList<>();
    private final ArrayList<String> properties = new ArrayList<>();

    public MessageListApiModel(final MessageListServiceModel data) {

        if (data == null) return;

        for (MessageServiceModel message : data.getMessages()) {
            this.items.add(new MessageApiModel(message));
        }

        for (String s : data.getProperties()) {
            this.properties.add(s);
        }
    }

    @JsonProperty("Items")
    public ArrayList<MessageApiModel> getItems() {
        return this.items;
    }

    @JsonProperty("Properties")
    public ArrayList<String> getProperties() {
        return this.properties;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "MessageList;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/messages");
        }};
    }
}
