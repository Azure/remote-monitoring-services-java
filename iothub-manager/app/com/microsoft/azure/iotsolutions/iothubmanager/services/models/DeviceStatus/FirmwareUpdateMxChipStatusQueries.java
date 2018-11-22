// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class FirmwareUpdateMxChipStatusQueries
{
    public static Map<DeviceStatusQueries.QueryType, String> queries =
            new HashMap<DeviceStatusQueries.QueryType, String>() {
        {
            put(DeviceStatusQueries.QueryType.APPLIED, "select * from devices where " +
                    "configurations.[[%s]].status = 'Applied'");
            put(DeviceStatusQueries.QueryType.SUCCESSFUL, "select * FROM devices WHERE " +
                    "properties.reported.firmware.fwUpdateStatus='Current' AND " +
                    "properties.reported.firmware.type='IoTDevKit'");
            put(DeviceStatusQueries.QueryType.FAILED, "select * FROM devices WHERE " +
                    "properties.reported.firmware.fwUpdateStatus='Error' AND " +
                    "properties.reported.firmware.type='IoTDevKit'");
        }
    };
}