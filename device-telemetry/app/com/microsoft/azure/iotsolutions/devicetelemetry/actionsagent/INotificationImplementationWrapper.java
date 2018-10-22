// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionExecutor;

public interface INotificationImplementationWrapper {
    IActionExecutor getImplementationType(INotification.EmailImplementationTypes actionType);
}
