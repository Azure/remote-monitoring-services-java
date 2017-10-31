// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import org.joda.time.DateTime;

import java.util.ArrayList;

@ImplementedBy(Alarms.class)
public interface IAlarms {
    AlarmServiceModel get(String id) throws Exception;

    ArrayList<AlarmServiceModel> getListByRuleId(
        String id,
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] devices
    ) throws Exception;

    int getCountByRuleId(
        String ruleId,
        DateTime from,
        DateTime to,
        String[] devices) throws Exception;

    ArrayList<AlarmServiceModel> getList(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] devices
    ) throws Exception;

    AlarmServiceModel update(String id, String status) throws Exception;
}
