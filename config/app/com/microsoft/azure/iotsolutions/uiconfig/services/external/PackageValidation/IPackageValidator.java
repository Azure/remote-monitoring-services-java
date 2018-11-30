// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidInputException;

public interface IPackageValidator {
    JsonNode getPackageContent(String pckg) throws InvalidInputException;

    Boolean validate();
}
