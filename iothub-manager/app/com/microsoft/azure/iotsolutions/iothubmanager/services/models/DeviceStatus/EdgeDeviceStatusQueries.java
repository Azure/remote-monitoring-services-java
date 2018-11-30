// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class EdgeDeviceStatusQueries {
    public static Map<DeviceStatusQueries.QueryType, String> queries =
            new HashMap<DeviceStatusQueries.QueryType,String>() {
        {
            put(DeviceStatusQueries.QueryType.APPLIED, "SELECT * from devices.modules WHERE " +
                    " moduleId = '$edgeAgent' " +
                    " AND configurations.[[%s]].status = 'Applied'");
            put(DeviceStatusQueries.QueryType.SUCCESSFUL, "SELECT * from devices.modules WHERE " +
                    " moduleId = '$edgeAgent' " +
                    " AND configurations.[[%s]].status = 'Applied' " +
                    " AND properties.desired.$version = properties.reported.lastDesiredVersion " +
                    " AND properties.reported.lastDesiredStatus.code = 200");
            put(DeviceStatusQueries.QueryType.FAILED, "SELECT * from devices WHERE " +
                    " moduleId = '$edgeAgent' " +
                    " AND configurations.[[%s]].status = 'Applied' " +
                    " AND properties.desired.$version = properties.reported.lastDesiredVersion " +
                    " AND properties.reported.lastDesiredStatus.code != 200");
        }
    };
}
