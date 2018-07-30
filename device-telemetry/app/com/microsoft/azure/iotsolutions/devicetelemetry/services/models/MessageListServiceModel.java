// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import java.util.ArrayList;

public class MessageListServiceModel {

    ArrayList<MessageServiceModel> messages = new ArrayList<>();
    ArrayList<String> properties = new ArrayList<>();

    public MessageListServiceModel(
        ArrayList<MessageServiceModel> messages,
        ArrayList<String> properties) {

        if (messages != null) this.messages = messages;
        if (properties != null) this.properties = properties;
    }

    public ArrayList<MessageServiceModel> getMessages() {
        return messages;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }
}
