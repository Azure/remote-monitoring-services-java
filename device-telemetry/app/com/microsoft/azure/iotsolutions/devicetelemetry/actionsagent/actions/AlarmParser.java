// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmsApiModel;
import play.Logger;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class AlarmParser {

    private static final Logger.ALogger logger = Logger.of(AlarmParser.class);

    public static List<AsaAlarmsApiModel> parseAlarmList(String alarms) {
        List<AsaAlarmsApiModel> alarmList = new ArrayList<>();
        if (alarms != null && !alarms.isEmpty()) {
            try {
                BufferedReader sr = new BufferedReader(new StringReader(alarms));
                String line = sr.readLine();
                while (line != null) {
                    alarmList.add(Json.fromJson(Json.parse(line), AsaAlarmsApiModel.class));
                }
            } catch (Exception e) {
                logger.error("Exception parsing the json string. Expected string in format {alarm}{alarm}...{alarm}");
            }
        }

        return alarmList;
    }
}
