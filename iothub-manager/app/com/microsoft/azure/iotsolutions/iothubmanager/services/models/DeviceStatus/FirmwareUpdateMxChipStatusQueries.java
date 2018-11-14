package com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class FirmwareUpdateMxChipStatusQueries
{
    public static Map<DeviceStatusQueries.QueryType, String> queries = new HashMap<DeviceStatusQueries.QueryType, String>() {
        {
            put(DeviceStatusQueries.QueryType.APPLIED, "SELECT deviceId from devices where configurations.[[firmware-update-2]].status = 'Applied'");
            put(DeviceStatusQueries.QueryType.SUCCESSFUL, "SELECT deviceId FROM devices WHERE properties.reported.firmware.fwUpdateStatus='Current' AND properties.reported.firmware.type='IoTDevKit'");
            put(DeviceStatusQueries.QueryType.FAILED, "SELECT deviceId FROM devices WHERE properties.reported.firmware.fwUpdateStatus='Error' AND properties.reported.firmware.type='IoTDevKit'");
        }
    };
}