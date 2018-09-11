// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.TimeSeriesParseException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import org.joda.time.DateTime;

@ImplementedBy(TimeSeriesClient.class)
public interface ITimeSeriesClient {

    Status ping();

    MessageListServiceModel queryEvents(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] deviceIds) throws TimeSeriesParseException, InvalidConfigurationException;
}
