package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.helpers;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;
import com.microsoft.azure.sdk.iot.service.Configuration;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;

import static play.libs.Json.fromJson;

public class PackagesHelper {

    public static boolean verifyPackageType(String packageContent, String packageType) {
        if (packageType.equals(PackageType.edgeManifest.toString()) && isEdgePackage(packageContent)) {
            return true;
        } else if (packageType.equals(PackageType.deviceConfiguration.toString())
                && !(isEdgePackage(packageContent))) {
            return true;
        }

        return false;
    }

    public static boolean isEdgePackage(String packageContent) {
        final Configuration pckgContent = fromJson(Json.parse(packageContent), Configuration.class);

        if (MapUtils.isNotEmpty(pckgContent.getContent().getModulesContent()) &&
                MapUtils.isEmpty(pckgContent.getContent().getDeviceContent())) {
            return true;
        }
        return false;
    }
}
