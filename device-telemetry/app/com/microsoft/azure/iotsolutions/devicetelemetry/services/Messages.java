// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;

import java.util.ArrayList;

public final class Messages implements IMessages {

    @Inject
    public Messages() {}

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

        sampleMessages.add(new MessageServiceModel("Elevator1", "2017-01-13T21:39:45-08:00", "1,up,5.8"));
        sampleMessages.add(new MessageServiceModel("Elevator2", "2017-01-14T03:13:04-08:00", "2,up,2.2"));
        sampleMessages.add(new MessageServiceModel("Elevator3", "2017-01-15T11:04:25-08:00", "1,down,0.2"));

        return sampleMessages;
    }
}
