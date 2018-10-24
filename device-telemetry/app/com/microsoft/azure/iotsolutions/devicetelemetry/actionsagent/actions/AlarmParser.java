// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import play.Logger;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class AlarmParser {

    private static final Logger.ALogger logger = Logger.of(AlarmParser.class);

    /**
     * Parse alarm list emitted by asa into event hub.
     * Alarms come in format:
     * {alarm1}{alarm2}...{alarmN}
     * @param alarms a string representation for a list of alarms
     * @return a list of {@link AsaAlarmApiModel} objects
     */
    public static List<AsaAlarmApiModel> parseAlarmList(String alarms) {
        List<AsaAlarmApiModel> alarmList = new ArrayList<>();
        if (alarms != null && !alarms.isEmpty()) {
            try {
                BufferedReader sr = new BufferedReader(new StringReader(alarms));
                String line = sr.readLine();
                logger.info(line);
                while (line != null) {
                    alarmList.add(Json.fromJson(Json.parse(line), AsaAlarmApiModel.class));
                    line = sr.readLine();
                }
            } catch (Exception e) {
                logger.error("Exception parsing the json string. Expected string in format {alarm}{alarm}...{alarm}");
            }
        }

        return alarmList;
    }
}
