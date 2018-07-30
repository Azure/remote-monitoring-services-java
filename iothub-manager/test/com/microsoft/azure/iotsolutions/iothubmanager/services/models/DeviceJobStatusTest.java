// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import helpers.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class DeviceJobStatusTest {

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void fromValueTest() {
        Assert.assertEquals(DeviceJobStatus.pending, DeviceJobStatus.from(0));
        Assert.assertEquals(DeviceJobStatus.scheduled, DeviceJobStatus.from(1));
        Assert.assertEquals(DeviceJobStatus.running, DeviceJobStatus.from(2));
        Assert.assertEquals(DeviceJobStatus.completed, DeviceJobStatus.from(3));
        Assert.assertEquals(DeviceJobStatus.failed, DeviceJobStatus.from(4));
        Assert.assertEquals(DeviceJobStatus.cancelled, DeviceJobStatus.from(5));
    }

    @Test(timeout = 100000, expected = IllegalArgumentException.class)
    @Category({UnitTest.class})
    public void fromValueFailureTest() {
        DeviceJobStatus.from(8);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void fromAzureJobStatusTest() {
        Assert.assertEquals(
            DeviceJobStatus.pending,
            DeviceJobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.enqueued));
        Assert.assertEquals(
            DeviceJobStatus.scheduled,
            DeviceJobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.scheduled));
        Assert.assertEquals(
            DeviceJobStatus.running,
            DeviceJobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.running));
        Assert.assertEquals(
            DeviceJobStatus.completed,
            DeviceJobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.completed));
        Assert.assertEquals(
            DeviceJobStatus.failed,
            DeviceJobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.failed));
        Assert.assertEquals(
            DeviceJobStatus.cancelled,
            DeviceJobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.cancelled));
    }
}
