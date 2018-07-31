// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.INotificationImplementation;

public interface INotificationImplementationWrapper {
    public INotificationImplementation getImplementationType(INotification.EmailImplementationTypes actionType);
}
