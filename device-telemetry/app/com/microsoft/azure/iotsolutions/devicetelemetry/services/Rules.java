// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;

import java.util.ArrayList;

public final class Rules implements IRules {

    @Inject
    public Rules() {
    }

    public RuleServiceModel get(String id) {
        return this.getSampleRule();
    }

    public ArrayList<RuleServiceModel> getList() {
        return this.getSampleRules();
    }

    public RuleServiceModel post(RuleServiceModel ruleServiceModel) {
        return this.getSampleRule();
    }

    public RuleServiceModel put(RuleServiceModel ruleServiceModel) {
        return this.getSampleRule();
    }

    public void delete(String id) {
        return;
    }

    /**
     * Get sample rules to return to client.
     * TODO: remove after storage dependency is added
     *
     * @return sample rules array
     */
    private RuleServiceModel getSampleRule() {
        // Init sample rule
        ArrayList<ConditionServiceModel> sampleConditionList = new ArrayList<>();
        ConditionServiceModel sampleCondition = null;
        sampleConditionList.add(sampleCondition);

        ArrayList<String> sampleEmails = new ArrayList<String>();
        sampleEmails.add("janedoe@contoso.com");
        sampleEmails.add("johndoe@contoso.com");

        // Sample elevator-floor-error
        sampleConditionList.set(0, new ConditionServiceModel(
            "floor",
            "building502-elevators",
            "GreaterThan",
            "7"));
        sampleConditionList.add(new ConditionServiceModel(
            "floor",
            "building502-elevators",
            "LessThan",
            "0"));
        return new RuleServiceModel(
            "6l1log0f7h2yt6p",
            "sample-id",
            "Elevator Floor Too High",
            "2017-02-22T22:22:22-08:00",
            "2017-03-12T22:03:00-08:00",
            true,
            "floor value is higher than 7",
            sampleConditionList);
    }

    /**
     * Get sample rules to return to client.
     * TODO: remove after storage dependency is added
     *
     * @return sample rules array
     */
    private ArrayList<RuleServiceModel> getSampleRules() {
        ArrayList<RuleServiceModel> sampleRules = new ArrayList<>();

        // Init default sample data
        ArrayList<ConditionServiceModel> sampleConditionList = new ArrayList<>();
        ConditionServiceModel sampleCondition = null;
        sampleConditionList.add(sampleCondition);

        ArrayList<String> sampleEmails = new ArrayList<String>();
        sampleEmails.add("janedoe@contoso.com");
        sampleEmails.add("johndoe@contoso.com");

        // Sample elevator-floor-error
        sampleConditionList.set(0, new ConditionServiceModel(
            "floor",
            "building502-elevators",
            "GreaterThan",
            "7"));
        sampleConditionList.add(new ConditionServiceModel(
            "floor",
            "building502-elevators",
            "LessThan",
            "0"));

        sampleRules.add(new RuleServiceModel(
            "6l1log0f7h2yt6p",
            "5e503de7-0c57-4902-8654-dc82357360d1",
            "Elevator Floor Too High",
            "2017-02-22T22:22:22-08:00",
            "2017-03-12T22:03:00-08:00",
            true,
            "floor value is higher than 7",
            sampleConditionList));

        // Sample elevator-speed-error
        sampleConditionList.set(0, new ConditionServiceModel(
            "speed",
            "building502-elevators",
            "GreaterThan",
            "20"));

        sampleRules.add(new RuleServiceModel(
            "kkru1d1ouqahpmg",
            "92aaadb3-1f89-4bde-aace-5691f0b0e337",
            "Elevator Speed Error",
            "2017-01-11T11:11:11-08:00",
            "2017-04-11T01:14:26-08:00",
            false,
            "speed is > 20mph",
            sampleConditionList));

        // Sample cable-temp-error
        sampleConditionList.set(0, new ConditionServiceModel(
            "temperature",
            "building502-elevators",
            "GreaterThan",
            "110"));

        sampleRules.add(new RuleServiceModel(
            "yoyzac6ovqq43w4",
            "d1b8c389-ef50-4988-87ef-446dbb48ce4d",
            "Elevator Cable Temp Error",
            "2017-03-13T13:31:13-08:00",
            "2017-04-11T01:20:04-08:00",
            true,
            "temperature of cables is above 110 deg F",
            sampleConditionList));

        return sampleRules;
    }

}
