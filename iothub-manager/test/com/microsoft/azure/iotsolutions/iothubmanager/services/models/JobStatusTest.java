// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;

public class JobStatusTest {

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void fromValueTest() {
        Assert.assertEquals(JobStatus.unknown, JobStatus.from(0));
        Assert.assertEquals(JobStatus.enqueued, JobStatus.from(1));
        Assert.assertEquals(JobStatus.running, JobStatus.from(2));
        Assert.assertEquals(JobStatus.completed, JobStatus.from(3));
        Assert.assertEquals(JobStatus.failed, JobStatus.from(4));
        Assert.assertEquals(JobStatus.cancelled, JobStatus.from(5));
        Assert.assertEquals(JobStatus.scheduled, JobStatus.from(6));
        Assert.assertEquals(JobStatus.queued, JobStatus.from(7));
    }

    @Test(timeout = 100000, expected = IllegalArgumentException.class)
    @Category({UnitTest.class})
    public void fromValueFailureTest() {
        JobStatus.from(8);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void fromAzureModelTest() {
        Assert.assertEquals(
            JobStatus.unknown,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.unknown));
        Assert.assertEquals(
            JobStatus.enqueued,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.enqueued));
        Assert.assertEquals(
            JobStatus.running,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.running));
        Assert.assertEquals(
            JobStatus.completed,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.completed));
        Assert.assertEquals(
            JobStatus.failed,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.failed));
        Assert.assertEquals(
            JobStatus.cancelled,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.cancelled));
        Assert.assertEquals(
            JobStatus.scheduled,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.scheduled));
        Assert.assertEquals(
            JobStatus.queued,
            JobStatus.fromAzureJobStatus(
                com.microsoft.azure.sdk.iot.service.jobs.JobStatus.queued));
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void toAzureModelTest() {
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.unknown,
            JobStatus.toAzureJobStatus(JobStatus.unknown));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.enqueued,
            JobStatus.toAzureJobStatus(JobStatus.enqueued));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.running,
            JobStatus.toAzureJobStatus(JobStatus.running));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.completed,
            JobStatus.toAzureJobStatus(JobStatus.completed));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.failed,
            JobStatus.toAzureJobStatus(JobStatus.failed));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.cancelled,
            JobStatus.toAzureJobStatus(JobStatus.cancelled));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.scheduled,
            JobStatus.toAzureJobStatus(JobStatus.scheduled));
        Assert.assertEquals(
            com.microsoft.azure.sdk.iot.service.jobs.JobStatus.queued,
            JobStatus.toAzureJobStatus(JobStatus.queued));
    }

    @Test(timeout = 100000, expected = IllegalArgumentException.class)
    @Category({UnitTest.class})
    public void toAzureModelWithNullValueTest() {
        JobStatus.toAzureJobStatus(null);
    }

}
