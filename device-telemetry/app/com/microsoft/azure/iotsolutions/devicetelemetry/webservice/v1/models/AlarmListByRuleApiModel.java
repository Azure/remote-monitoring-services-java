// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class AlarmListByRuleApiModel extends AlarmListApiModel{

    public AlarmListByRuleApiModel(ArrayList<AlarmServiceModel> alarms) {
        super(alarms);
    }

    @JsonProperty("$metadata")
    @Override
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "AlarmsByRule;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/alarmsbyrule");
        }};
    }
}
