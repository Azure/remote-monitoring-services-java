package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus.FirmwareUpdateMxChipStatusQueries;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceStatus.EdgeDeviceStatusQueries;
import java.util.HashMap;
import java.util.Map;

public class DeviceStatusQueries {

    private static Map<ConfigType, Map<DeviceStatusQueries.QueryType, String>> admQueryMapping =
            new HashMap<ConfigType, Map<DeviceStatusQueries.QueryType, String>>() {
        {
            put(ConfigType.firmwareUpdateMxChip, FirmwareUpdateMxChipStatusQueries.queries);
        }
    };

    public static Map<QueryType, String> getQueries(String deploymentType, String configType)
    {
        if (deploymentType.equals(DeploymentType.edgeManifest.toString()))
        {
            return EdgeDeviceStatusQueries.queries;
        }

        return admQueryMapping.getOrDefault(ConfigType.valueOf(configType), new HashMap<>());
    }

    public enum QueryType { APPLIED, SUCCESSFUL, FAILED };
}
