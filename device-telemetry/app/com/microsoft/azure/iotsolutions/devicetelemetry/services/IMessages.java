// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;

import java.util.ArrayList;

@ImplementedBy(Messages.class)
public interface IMessages {

    ArrayList<MessageServiceModel> getList();
}
