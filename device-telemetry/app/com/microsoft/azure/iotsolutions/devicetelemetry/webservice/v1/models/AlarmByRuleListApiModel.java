// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmCountByRuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class AlarmByRuleListApiModel {
    private final ArrayList<AlarmByRuleApiModel> items;

    public AlarmByRuleListApiModel(final List<AlarmCountByRuleServiceModel> alarms) {
        this.items = new ArrayList<>();

        if (alarms != null) {
            for (AlarmCountByRuleServiceModel alarm : alarms) {
                items.add(new AlarmByRuleApiModel(
                    alarm.getCount(),
                    alarm.getStatus(),
                    alarm.getMessageTime(),
                    new AlarmRuleApiModel(alarm.getRule())
                ));
            }
        }
    }

    @JsonProperty("Items")
    public ArrayList<AlarmByRuleApiModel> getItems() {
        return this.items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "AlarmsByRule;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/alarmsbyrule");
        }};
    }
}
