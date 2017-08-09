// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;
import org.joda.time.DateTime;

import java.util.*;

public class AlarmByRuleListApiModel {
    private final ArrayList<AlarmByRuleApiModel> items;

    private enum Status {
        open  (3),
        acknowledged(2),
        closed   (1);

        private final int statusCode;

        private Status(int statusCode) {
            this.statusCode = statusCode;
        }
    }

    public AlarmByRuleListApiModel(final ArrayList<AlarmServiceModel> alarms) {
        this.items = new ArrayList<>();
        if (alarms != null) {
            HashMap<String, AlarmByRuleApiModel> map = new HashMap<>();
            for (AlarmServiceModel alarm : alarms) {
                String id = alarm.getRuleId();
                if(!map.containsKey(id)) {
                    map.put(id, new AlarmByRuleApiModel(1, alarm.getStatus(), alarm.getDateModified(), alarm));
                } else {
                    AlarmByRuleApiModel alarmByRule = map.get(id);
                    alarmByRule.setCount(alarmByRule.getCount() + 1);
                    DateTime created = alarm.getDateCreated();
                    // We only want to update the created date with the latest timestamp
                    if(DateTime.parse(alarmByRule.getCreated()).isBefore(created)) {
                        alarmByRule.setCreated(created);
                    }

                    String savedStatus = alarmByRule.getStatus();
                    String newStatus = alarm.getStatus();
                    // The status value that gets set in case of multiple alarms
                    // with different status follows priority order:
                    // 1) Open, 2) Acknowledged and 3) Closed
                    if(Status.valueOf(savedStatus).statusCode < Status.valueOf(newStatus).statusCode) {
                        alarmByRule.setStatus(newStatus);
                    }
                }
            }

            Collection<AlarmByRuleApiModel> values = map.values();
            for (AlarmByRuleApiModel value: values) {
                this.items.add(value);
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
