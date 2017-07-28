// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import org.joda.time.DateTime;

@ImplementedBy(Messages.class)
public interface IMessages {

    MessageListServiceModel getList(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] devices);
}
