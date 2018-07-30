// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.RecurringTasksAgent;

import com.google.inject.ImplementedBy;

@ImplementedBy(Agent.class)
public interface IRecurringTasksAgent {
    void run();
}
