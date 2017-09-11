// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;

public class JobTypeTest {

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void fromValueTest() {
        Assert.assertEquals(JobType.unknown, JobType.from(0));
        Assert.assertEquals(JobType.scheduleDeviceMethod, JobType.from(3));
        Assert.assertEquals(JobType.scheduleUpdateTwin, JobType.from(4));
    }

    @Test(timeout = 100000, expected = IllegalArgumentException.class)
    @Category({UnitTest.class})
    public void fromValueFailureTest() {
        Assert.assertEquals(JobType.unknown, JobType.from(10));
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void fromAzureModelTest() {
        Assert.assertEquals(
            JobType.scheduleDeviceMethod,
            JobType.fromAzureJobType(
                com.microsoft.azure.sdk.iot.service.jobs.JobType.scheduleDeviceMethod));
        Assert.assertEquals(
            JobType.scheduleUpdateTwin,
            JobType.fromAzureJobType(
                com.microsoft.azure.sdk.iot.service.jobs.JobType.scheduleUpdateTwin));
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void toAzureModelTest() {
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobType.scheduleDeviceMethod,
            JobType.toAzureJobType(JobType.scheduleDeviceMethod));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobType.scheduleUpdateTwin,
            JobType.toAzureJobType(JobType.scheduleUpdateTwin));
    }
}
