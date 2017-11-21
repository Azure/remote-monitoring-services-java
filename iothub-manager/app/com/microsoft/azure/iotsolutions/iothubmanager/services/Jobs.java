// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IConfigService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.jobs.JobClient;
import com.microsoft.azure.sdk.iot.service.jobs.JobResult;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Jobs implements IJobs {

    private static final Logger.ALogger log = Logger.of(Jobs.class);

    private IIoTHubWrapper ioTHubService;
    private final IConfigService configService;
    private final JobClient jobClient;

    private final String DEVICE_DETAILS_QUERY_FORMAT = "select * from devices.jobs where devices.jobs.jobId = '%s'";
    private final String DEVICE_DETAILS_QUERYWITH_STATUS_FORMAT = "select * from devices.jobs where devices.jobs.jobId = '%s' and devices.jobs.status = '%s'";

    @Inject
    public Jobs(final IIoTHubWrapper ioTHubService, final IConfigService configService) throws Exception {
        this.ioTHubService = ioTHubService;
        this.configService = configService;
        this.jobClient = ioTHubService.getJobClient();
    }

    @Override
    public CompletionStage<List<JobServiceModel>> getJobsAsync(
        JobType jobType,
        JobStatus jobStatus,
        Integer pageSize,
        long from, long to)
        throws InvalidInputException, ExternalDependencyException {
        try {
            Query query = this.jobClient.queryJobResponse(
                jobType == null ? null : JobType.toAzureJobType(jobType),
                jobStatus == null ? null : JobStatus.toAzureJobStatus(jobStatus),
                pageSize);

            List jobs = new ArrayList<JobResult>();
            while (this.jobClient.hasNextJob(query)) {
                JobResult job = this.jobClient.getNextJob(query);
                if (job.getCreatedTime().getTime() >= from && job.getCreatedTime().getTime() <= to) {
                    jobs.add(new JobServiceModel(job, null));
                }
            }
            return CompletableFuture.supplyAsync(() -> jobs);
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to query device jobs by: %s, %s, %d", jobType, jobStatus, pageSize);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    @Override
    public CompletionStage<JobServiceModel> getJobAsync(
        String jobId,
        boolean includeDeviceDetails,
        DeviceJobStatus devicejobStatus)
        throws ExternalDependencyException {
        try {
            JobResult result = this.jobClient.getJob(jobId);
            JobServiceModel jobModel;
            if (!includeDeviceDetails) {
                jobModel = new JobServiceModel(result, null);
            } else {
                String queryString = devicejobStatus == null ? String.format(DEVICE_DETAILS_QUERY_FORMAT, jobId) :
                    String.format(DEVICE_DETAILS_QUERYWITH_STATUS_FORMAT, jobId, devicejobStatus);
                Query query = this.jobClient.queryDeviceJob(queryString);
                List deviceJobs = new ArrayList<JobServiceModel>();
                while (this.jobClient.hasNextJob(query)) {
                    JobResult deviceJob = this.jobClient.getNextJob(query);
                    deviceJobs.add(deviceJob);
                }
                jobModel = new JobServiceModel(result, deviceJobs);
            }
            return CompletableFuture.supplyAsync(() -> jobModel);
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to get device job by id: %s", jobId);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    @Override
    public CompletionStage<JobServiceModel> scheduleDeviceMethodAsync(
        String jobId,
        String queryCondition,
        MethodParameterServiceModel parameter,
        Date startTime,
        long maxExecutionTimeInSeconds)
        throws ExternalDependencyException {
        try {
            JobResult result = this.jobClient.scheduleDeviceMethod(
                jobId,
                queryCondition,
                parameter.getName(),
                parameter.getResponseTimeout() == null ? null : parameter.getResponseTimeout().getSeconds(),
                parameter.getConnectionTimeout() == null ? null : parameter.getConnectionTimeout().getSeconds(),
                parameter.getJsonPayload(),
                startTime,
                maxExecutionTimeInSeconds);
            JobServiceModel jobModel = new JobServiceModel(result, null);
            return CompletableFuture.supplyAsync(() -> jobModel);
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to schedule device method job: %s, %s, %s",
                jobId, queryCondition, Json.stringify(Json.toJson(parameter)));
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    @Override
    public CompletionStage<JobServiceModel> scheduleTwinUpdateAsync(
        String jobId,
        String queryCondition,
        DeviceTwinServiceModel twin,
        Date startTime,
        long maxExecutionTimeInSeconds)
        throws ExternalDependencyException {
        try {
            // Update the deviceGroupFilter cache, no need to wait
            this.configService.updateDeviceGroupFiltersAsync(twin);

            JobResult result = this.jobClient.scheduleUpdateTwin(
                jobId,
                queryCondition,
                twin.toDeviceTwinDevice(),
                startTime,
                maxExecutionTimeInSeconds);
            JobServiceModel jobModel = new JobServiceModel(result, null);
            return CompletableFuture.supplyAsync(() -> jobModel);
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to schedule twin update job: %s, %s, %s",
                jobId, queryCondition, Json.stringify(Json.toJson(twin)));
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }
}
