// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation;

import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;

public class PackageValidatorFactory
{
    public static IPackageValidator GetValidator(PackageType packageType, String config)
    {
        if (packageType.equals(PackageType.edgeManifest))
        {
            return new EdgePackageValidator();
        }

        if (config.equalsIgnoreCase(ConfigType.firmwareUpdate.toString()))
        {
            return new FirmwareUpdateMxChipValidator();
        }
        else
        {
            return null;
        }
    }
}
