// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class MessageListApiModel {
    private final ArrayList<MessageApiModel> items;

    public MessageListApiModel(final ArrayList<MessageServiceModel> messages) {
        this.items = new ArrayList<>();
        if (!messages.isEmpty()) {
            for (MessageServiceModel message : messages) {
                this.items.add(new MessageApiModel(message));
            }
        }
    }

    @JsonProperty("Items")
    public ArrayList<MessageApiModel> getItems() {
        return items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "MessageList;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/messages");
        }};
    }
}
