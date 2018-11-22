// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatusQueries;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultDeviceStatus {

    public static Map<DeviceStatusQueries.QueryType, String> queries = new HashMap<DeviceStatusQueries.QueryType, String>()
    {
        {
            put(DeviceStatusQueries.QueryType.APPLIED, "select * from devices where " +
                        "configurations.[[%s]].status = 'Applied'");
            put(DeviceStatusQueries.QueryType.SUCCESSFUL, StringUtils.EMPTY);
            put(DeviceStatusQueries.QueryType.FAILED, StringUtils.EMPTY);
        }
    };
}