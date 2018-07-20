// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation;

import java.util.List;

public interface IImplementation {
    public void setReceiver(List<String> receivers);

    public void setMessage(String message, String ruleId, String ruleDescription);

    public void execute();
}
