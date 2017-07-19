// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;

import java.util.ArrayList;

@ImplementedBy(AlarmsByRule.class)
public interface IAlarmsByRule {
    AlarmServiceModel get(String id);

    ArrayList<AlarmServiceModel> getList();
}
