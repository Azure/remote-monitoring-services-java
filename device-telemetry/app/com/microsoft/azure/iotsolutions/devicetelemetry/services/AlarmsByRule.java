// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmRuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;

import java.util.ArrayList;

public class AlarmsByRule implements IAlarmsByRule {
    @Inject
    public AlarmsByRule() {
    }

    @Override
    public AlarmServiceModel get(String id) {
        return this.getSampleAlarm();
    }

    @Override
    public ArrayList<AlarmServiceModel> getList() {
        return this.getSampleAlarms();
    }

    /**
     * Get sample alarm to return to client.
     * TODO: remove after storage dependency is added
     *
     * @return sample alarm
     */
    private AlarmServiceModel getSampleAlarm() {
        return new AlarmServiceModel(
                "6l1log0f7h2yt6p",
                "1234",
                "2017-02-22T22:22:22-08:00",
                "2017-02-22T22:22:22-08:00",
                "Temperature on device x > 75 deg F",
                "group-Id",
                "device-id",
                "critical",
                "open",
                new AlarmRuleServiceModel("1234", "HVAC temp > 75" )
        );
    }

    /**
     * Get sample alarms to return to client.
     * TODO: remove after storage dependency is added
     *
     * @return sample alarm list
     */
    private ArrayList<AlarmServiceModel> getSampleAlarms() {
        ArrayList<AlarmServiceModel> list = new ArrayList<AlarmServiceModel>();
        AlarmRuleServiceModel rule = new AlarmRuleServiceModel("1234", "HVAC temp > 75" );
        AlarmServiceModel alarm1 = new AlarmServiceModel(
                "6l1log0f7h2yt6p",
                "1234",
                "2017-02-22T22:22:22-08:00",
                "2017-02-22T22:22:22-08:00",
                "Temperature on device x > 75 deg F",
                "group-Id",
                "device-id",
                "critical",
                "open",
                rule
        );
        AlarmServiceModel alarm2 = new AlarmServiceModel(
                "2h2yt6p",
                "1234",
                "2017-02-22T22:22:22-08:00",
                "2017-02-22T22:22:22-08:00",
                "Temperature on device x > 75 deg F",
                "group-Id",
                "device-id",
                "critical",
                "acknowledged",
                rule
        );
        AlarmServiceModel alarm3 = new AlarmServiceModel(
                "6l1log0f7h2yt6p",
                "1234",
                "2017-02-22T22:22:22-08:00",
                "2017-02-22T22:22:22-08:00",
                "Temperature on device x > 75 deg F",
                "group-Id",
                "device-id",
                "info",
                "open",
                rule
        );
        AlarmServiceModel alarm4 = new AlarmServiceModel(
                "6l1log0f7h2yt6p",
                "1234",
                "2017-02-22T22:22:22-08:00",
                "2017-02-22T22:22:22-08:00",
                "Temperature on device x > 75 deg F",
                "group-Id",
                "device-id",
                "warning",
                "closed",
                rule
        );
        list.add(alarm1);
        list.add(alarm2);
        list.add(alarm3);
        list.add(alarm4);
        return list;
    }
}
