package com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation;

import com.fasterxml.jackson.databind.JsonNode;

public interface IPackageValidator {
    JsonNode getPackageContent(String pckg);

    Boolean validate();
}
