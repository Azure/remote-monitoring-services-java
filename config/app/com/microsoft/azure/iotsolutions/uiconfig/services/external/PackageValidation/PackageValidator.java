package com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation;

import com.fasterxml.jackson.databind.JsonNode;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidInputException;
import play.libs.Json;

public abstract class PackageValidator implements IPackageValidator {


    @Override
    public JsonNode getPackageContent(String pckg) {
        try
        {
            return Json.parse(pckg);
        }
        catch (Exception e)
        {
            //throw new InvalidInputException("Provided package is not a valid json.");
            return null;
        }
    }

    @Override
    public abstract Boolean validate();
}
