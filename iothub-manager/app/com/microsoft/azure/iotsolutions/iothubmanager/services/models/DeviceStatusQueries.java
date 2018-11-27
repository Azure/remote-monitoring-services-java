// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus.DefaultDeviceStatus;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus.FirmwareStatusQueries;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus.EdgeDeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class DeviceStatusQueries {

    private static Map<String, Map<DeviceStatusQueries.QueryType, String>> admQueryMapping =
            new HashMap<String, Map<DeviceStatusQueries.QueryType, String>>() {
        {
            put(ConfigType.firmware.toString(), FirmwareStatusQueries.queries);
        }
    };

    public static Map<QueryType, String> getQueries(String deploymentType, String configType)
    {
        if (deploymentType.equals(PackageType.edgeManifest.toString()))
        {
            return EdgeDeviceStatusQueries.queries;
        }

        return admQueryMapping.getOrDefault(String.valueOf(configType), DefaultDeviceStatus.queries);
    }

    public enum QueryType { APPLIED, SUCCESSFUL, FAILED };
}
