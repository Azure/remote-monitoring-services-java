// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime.Config;
import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.util.*;

public class JobsTest {

    private static Config config;
    private static IServicesConfig servicesConfig;
    private static IIoTHubWrapper ioTHubWrapper;
    private static IDevices deviceService;
    private static IJobs jobService;
    private static ArrayList<DeviceServiceModel> testDevices = new ArrayList<>();
    private static String batchId = UUID.randomUUID().toString().replace("-", "");

    private static boolean setUpIsDone = false;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        if (setUpIsDone) {
            return;
        }

        config = new Config();
        servicesConfig = config.getServicesConfig();
        ioTHubWrapper = new IoTHubWrapper(servicesConfig);
        deviceService = new Devices(ioTHubWrapper);
        jobService = new Jobs(ioTHubWrapper);

        createTestDevices(2, batchId);

        setUpIsDone = true;
    }

    @AfterClass
    public static void tearDownOnce() {

    }

    // Ignore this test temporarily due to bug of SDK can not accept twin without
    // Device ID. Otherwise it will always complains "IllegalArgumentException:
    // Device ID cannot be null or empty". Will enable it once the bug is fixed.
    @Ignore
    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void scheduleTwinJobAsyncTest() throws Exception {
        String jobId = "unitTestJob" + batchId;
        String condition = String.format("tags.BatchId='%s'", batchId);
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
            put("Floor", "1F");
        }};
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel("etagxx==", "", null, tags, true);
        JobServiceModel job = jobService.scheduleTwinUpdateAsync(jobId, condition, twin, new Date(), 120).toCompletableFuture().get();

        JobServiceModel newJob = jobService.getJobAsync(jobId).toCompletableFuture().get();
        Assert.assertEquals(jobId, newJob.getJobId());
        Assert.assertEquals(JobType.scheduleUpdateTwin, newJob.getJobType());
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void scheduleMethodJobAsyncTest() throws Exception {
        String jobId = "unitTestJob" + batchId;
        String condition = String.format("tags.BatchId='%s'", batchId);
        MethodParameterServiceModel parameter = new MethodParameterServiceModel();
        parameter.setName("Reboot");
        parameter.setJsonPayload("{\"key1\": \"value1\"}");
        parameter.setResponseTimeout(Duration.ofSeconds(5));
        parameter.setConnectionTimeout(Duration.ofSeconds(5));
        JobServiceModel job = jobService.scheduleDeviceMethodAsync(jobId, condition, parameter, new Date(), 120).toCompletableFuture().get();
        Assert.assertEquals(jobId, job.getJobId());
        Assert.assertEquals(job.getJobType(), JobType.scheduleDeviceMethod);

        JobServiceModel newJob = jobService.getJobAsync(jobId).toCompletableFuture().get();
        Assert.assertEquals(jobId, newJob.getJobId());
        Assert.assertEquals(parameter.getName(), newJob.getMethodParameter().getName());
        Assert.assertEquals(parameter.getJsonPayload(), newJob.getMethodParameter().getJsonPayload());

        List<JobServiceModel> jobs = jobService.getJobsAsync(JobType.scheduleDeviceMethod, JobStatus.completed, 10).toCompletableFuture().get();
        if (jobs.size() > 0) {
            Assert.assertEquals(JobType.scheduleDeviceMethod, jobs.get(0).getJobType());
        }
    }

    private static void createTestDevices(int count, String batchId) {
        try {
            for (int i = 0; i < count; i++) {
                String deviceId = String.format("unitTestDevice-%s_%s", i, batchId);
                String eTag = "etagxx==";
                HashMap<String, Object> tags = new HashMap<String, Object>() {{
                    put("BatchId", batchId);
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
                System.out.println(deviceId + "created");
            }
        } catch (Exception e) {
            Assert.fail("Unable to create test deviceService");
        }
    }
}
