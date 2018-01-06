// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.external.ConfigService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IConfigService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime.Config;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubTooManyRequestsException;
import helpers.IntegrationTest;
import org.junit.*;
import org.junit.experimental.categories.Category;
import play.test.WSTestClient;

import java.time.Duration;
import java.util.*;

public class JobsTest {

    private static Config config;
    private static IServicesConfig servicesConfig;
    private static IConfigService configService;
    private static IIoTHubWrapper ioTHubWrapper;
    private static IDevices deviceService;
    private static IJobs jobService;
    private static ArrayList<DeviceServiceModel> testDevices = new ArrayList<>();
    private static String batchId = UUID.randomUUID().toString().replace("-", "");
    public static final int MAX_RETRIES = 10;

    private static boolean setUpIsDone = false;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        if (setUpIsDone) {
            return;
        }

        config = new Config();
        servicesConfig = config.getServicesConfig();
        configService = new ConfigService(servicesConfig, WSTestClient.newClient(9005));
        ioTHubWrapper = new IoTHubWrapper(servicesConfig);
        deviceService = new Devices(ioTHubWrapper, configService);
        jobService = new Jobs(ioTHubWrapper, configService);

        createTestDevices(2, batchId);

        setUpIsDone = true;
    }

    @AfterClass
    public static void tearDownOnce() {
        for (DeviceServiceModel device : testDevices) {
            try {
                deviceService.deleteAsync(device.getId()).toCompletableFuture().get();
                System.out.println(device.getId() + " deleted");
            } catch (Exception ex) {
                Assert.fail("Unable to destroy test deviceService");
            }
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void getJobsAsyncTest() throws Exception {
        long from = 0;
        long to = new Date().getTime();
        List<JobServiceModel> jobs = jobService.getJobsAsync(
            JobType.scheduleUpdateTwin, JobStatus.completed, 10, from, to).toCompletableFuture().get();
        if (jobs.size() > 0) {
            Assert.assertTrue(!jobs.get(0).getJobId().isEmpty());
            Assert.assertEquals(jobs.get(0).getJobType(), JobType.scheduleUpdateTwin);
            Assert.assertEquals(jobs.get(0).getJobStatus(), JobStatus.completed);
        }

        jobs = jobService.getJobsAsync(
            JobType.scheduleDeviceMethod, JobStatus.completed, 10, from, to).toCompletableFuture().get();
        if (jobs.size() > 0) {
            Assert.assertTrue(!jobs.get(0).getJobId().isEmpty());
            Assert.assertEquals(jobs.get(0).getJobType(), JobType.scheduleDeviceMethod);
            Assert.assertEquals(jobs.get(0).getJobStatus(), JobStatus.completed);
        }

        jobs = jobService.getJobsAsync(null, null, 10, from, to).toCompletableFuture().get();
        if (jobs.size() > 0) {
            Assert.assertTrue(!jobs.get(0).getJobId().isEmpty());
            Assert.assertNotNull(jobs.get(0).getJobType());
            Assert.assertNotNull(jobs.get(0).getJobStatus());
        }
    }

    @Test(timeout = 310000)
    @Category({IntegrationTest.class})
    public void scheduleTwinJobAsyncTest() throws Exception {
        String jobIdPrefix = "IntegrationTestTwinJob" + batchId;
        String condition = String.format("tags.BatchId='%s'", batchId);
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
            put("Floor", "1F");
        }};
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel("*", "", null, tags, true);

        IJobs twinJobService = new Jobs(ioTHubWrapper, configService);
        // retry scheduling job with back off time when throttled by IotHub
        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                String newJobId = jobIdPrefix + i;
                twinJobService.scheduleTwinUpdateAsync(newJobId, condition, twin, new Date(), 120).toCompletableFuture().get();
                JobServiceModel newJob = twinJobService.getJobAsync(newJobId, false, null).toCompletableFuture().get();
                Assert.assertEquals(newJobId, newJob.getJobId());
                Assert.assertEquals(JobType.scheduleUpdateTwin, newJob.getJobType());
                Assert.assertEquals(newJob.getUpdateTwin().getTags().get("Building"), "Building40");
                Assert.assertEquals(newJob.getUpdateTwin().getTags().get("Floor"), "1F");

                // Sleep for a few seconds before IoTHub returns the new job with consistent state
                Thread.sleep(5000);
                newJob = twinJobService.getJobAsync(newJobId, true, null).toCompletableFuture().get();
                DeviceJobServiceModel deviceJob = newJob.getDevices().get(0);
                Assert.assertNotNull(deviceJob.getDeviceId());
                Assert.assertNotNull(deviceJob.getStatus());

                return;
            } catch (Exception e) {
                if (e.getCause() instanceof IotHubTooManyRequestsException) {
                    System.out.println(String.format("Warning: job scheduling is throttled and will be retried(%d) after 30s", i));
                    Thread.sleep(30000);
                    // reconnect to IotHub
                    twinJobService = new Jobs(ioTHubWrapper, configService);
                    continue;
                } else {
                    Assert.fail(String.format("failed to schedule twin job due to %s", e.getCause().getMessage()));
                    return;
                }
            }
        }
        System.out.println(String.format("Warning: passed this test finally because of job throttled for %d time", MAX_RETRIES));
    }

    @Test(timeout = 310000)
    @Category({IntegrationTest.class})
    public void scheduleMethodJobAsyncTest() throws Exception {
        String jobIdPrefix = "IntegrationTestMethodJob" + batchId;
        String condition = String.format("tags.BatchId='%s'", batchId);
        MethodParameterServiceModel parameter = new MethodParameterServiceModel();
        parameter.setName("Reboot");
        parameter.setJsonPayload("{\"key1\": \"value1\"}");
        parameter.setResponseTimeout(Duration.ofSeconds(5));
        parameter.setConnectionTimeout(Duration.ofSeconds(5));

        JobServiceModel job;
        // retry scheduling job with back off time when throttled by IotHub
        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                String newJobId = jobIdPrefix + i;
                job = jobService.scheduleDeviceMethodAsync(newJobId, condition, parameter, new Date(), 10).toCompletableFuture().get();
                Assert.assertEquals(newJobId, job.getJobId());
                Assert.assertEquals(job.getJobType(), JobType.scheduleDeviceMethod);

                JobServiceModel newJob = jobService.getJobAsync(newJobId, false, null).toCompletableFuture().get();
                Assert.assertEquals(newJobId, newJob.getJobId());
                Assert.assertEquals(parameter.getName(), newJob.getMethodParameter().getName());
                Assert.assertEquals(parameter.getJsonPayload(), newJob.getMethodParameter().getJsonPayload());

                List<JobServiceModel> jobs = jobService.getJobsAsync(JobType.scheduleDeviceMethod, JobStatus.completed, 10, Long.MIN_VALUE, Long.MAX_VALUE).toCompletableFuture().get();
                if (jobs.size() > 0) {
                    Assert.assertEquals(JobType.scheduleDeviceMethod, jobs.get(0).getJobType());
                }
                return;
            } catch (Exception e) {
                if (e.getCause() instanceof IotHubTooManyRequestsException) {
                    System.out.println(String.format("Warning: job scheduling is throttled and will be retried(%d) after 30s", i));
                    Thread.sleep(30000);
                    // reconnect to IotHub
                    jobService = new Jobs(ioTHubWrapper, configService);
                    continue;
                } else {
                    Assert.fail("failed to schedule method job");
                    return;
                }
            }
        }
        System.out.println(String.format("Warning: passed this test finally because of job throttled for %d time", MAX_RETRIES));
    }

    private static void createTestDevices(int count, String batchId) {
        try {
            for (int i = 0; i < count; i++) {
                String deviceId = String.format("IntegrationTest_%s_%s", batchId, i);
                String eTag = "etagxx==";
                HashMap<String, Object> tags = new HashMap<String, Object>() {{
                    put("BatchId", batchId);
                    put("Purpose", "IntegrationTest");
                }};
                HashMap desired = new HashMap() {
                    {
                        put("Config", new HashMap<String, Object>() {
                            {
                                put("Test", 1);
                            }
                        });
                    }
                };
                DeviceTwinProperties properties = new DeviceTwinProperties(desired, null);
                DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, properties, tags, true);
                DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, null, null);
                DeviceServiceModel newDevice = deviceService.createAsync(device).toCompletableFuture().get();
                testDevices.add(newDevice);
                System.out.println(deviceId + " created");
            }
        } catch (Exception e) {
            Assert.fail("Unable to create test devices");
        }
    }
}
