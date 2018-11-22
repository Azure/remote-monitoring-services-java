// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class EdgeDeviceStatusQueries
{
    public static Map<DeviceStatusQueries.QueryType, String> queries =
            new HashMap<DeviceStatusQueries.QueryType,String>()
    {
        {
            put(DeviceStatusQueries.QueryType.APPLIED, "select * from devices.modules where moduleId = '$edgeAgent' " +
                    "and configurations.[[%s]].status = 'Applied'");
            put(DeviceStatusQueries.QueryType.SUCCESSFUL, "select * from devices.modules where moduleId = '$edgeAgent' " +
                    "and properties.desired.$version = properties.reported.lastDesiredVersion and " +
                    "properties.reported.lastDesiredStatus.code = 200");
            put(DeviceStatusQueries.QueryType.FAILED, "select * from devices WHERE " +
                    "properties.reported.firmware.fwUpdateStatus='Error' AND " +
                    "properties.reported.firmware.type='IoTDevKit'");
        }
    };
}
