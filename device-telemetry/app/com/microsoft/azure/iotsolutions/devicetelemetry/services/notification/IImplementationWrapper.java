// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.IImplementation;

public interface IImplementationWrapper {
    public IImplementation getImplementationType(INotification.EmailImplementationTypes actionType);
}
