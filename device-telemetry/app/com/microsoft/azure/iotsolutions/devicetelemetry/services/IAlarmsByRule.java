// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@ImplementedBy(AlarmsByRule.class)
public interface IAlarmsByRule {
    AlarmServiceModel get(String id, DateTime from, DateTime to, String order, int skip,
                          int limit, String[] devices) throws Exception;

    ArrayList<AlarmServiceModel> getList(DateTime from, DateTime to, String order, int skip,
                                         int limit, String[] devices) throws Exception;
}
