// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation;

import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;

public class PackageValidatorFactory {
    public static IPackageValidator GetValidator(PackageType packageType, String configType)
    {
        if (packageType.equals(PackageType.edgeManifest)) {
            return new EdgePackageValidator();
        }

        if (configType.equalsIgnoreCase(ConfigType.firmware.toString())) {
            return new FirmwareValidator();
        } else {
            return null;
        }
    }
}
