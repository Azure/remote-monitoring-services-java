// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class FirmwareStatusQueries {
    public static Map<DeviceStatusQueries.QueryType, String> queries =
            new HashMap<DeviceStatusQueries.QueryType, String>() {
        {
            put(DeviceStatusQueries.QueryType.APPLIED, "SELECT * from devices WHERE " +
                    " configurations.[[%s]].status = 'Applied'");
            put(DeviceStatusQueries.QueryType.SUCCESSFUL, "SELECT * FROM devices WHERE " +
                    " configurations.[[%s]].status = 'Applied'" +
                    " AND properties.reported.firmware.fwUpdateStatus='Current' " +
                    " AND properties.reported.firmware.type='IoTDevKit'");
            put(DeviceStatusQueries.QueryType.FAILED, "SELECT * FROM devices WHERE " +
                    " configurations.[[%s]].status = 'Applied'" +
                    " AND properties.reported.firmware.fwUpdateStatus='Error' " +
                    " AND properties.reported.firmware.type='IoTDevKit'");
        }
    };
}