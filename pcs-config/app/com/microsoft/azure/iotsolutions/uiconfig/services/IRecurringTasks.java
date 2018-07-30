// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;

@ImplementedBy(RecurringTasks.class)
public interface IRecurringTasks {
    void run();
}
