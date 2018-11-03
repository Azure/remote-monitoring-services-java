// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

import java.util.TreeMap;

public class EmailActionSettings implements IActionSettings{

    private static final String IS_ENABLED_KEY = "IsEnabled";
    private static final String OFFICE365_CONNECTOR_URL_KEY = "Office365ConnectorUrl";

    private ActionType type;
    private TreeMap<String, Object> parameters;

    @Inject
    public EmailActionSettings(){

    }

    @Override
    @JsonProperty("Type")
    public ActionType getType() {
        return null;
    }

    @Override
    @JsonProperty("Parameters")
    public TreeMap getSettings() {
        return null;
    }
}
