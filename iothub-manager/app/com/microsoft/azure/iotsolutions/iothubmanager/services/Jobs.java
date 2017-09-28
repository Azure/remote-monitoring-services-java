// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.jobs.JobClient;
import com.microsoft.azure.sdk.iot.service.jobs.JobResult;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.omg.CORBA.INVALID_ACTIVITY;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class Jobs implements IJobs {

    private static final Logger.ALogger log = Logger.of(Jobs.class);

    private IIoTHubWrapper ioTHubService;
    private final JobClient jobClient;

    @Inject
    public Jobs(final IIoTHubWrapper ioTHubService) throws Exception {
        this.ioTHubService = ioTHubService;
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

            // Remove this workaround block once the Java SDK support empty jobType and jobStatus.
            boolean workaround = true;
            if (workaround) {
                return getJobsWorkaroundAsync(jobType, jobStatus, pageSize, from, to);
            }

            Query query = this.jobClient.queryJobResponse(
                JobType.toAzureJobType(jobType),
                JobStatus.toAzureJobStatus(jobStatus),
                pageSize);

            List jobs = new ArrayList<JobResult>();
            while (this.jobClient.hasNextJob(query)) {
                JobResult job = this.jobClient.getNextJob(query);
                if (job.getCreatedTime().getTime() >= from && job.getCreatedTime().getTime() <= to) {
                    jobs.add(new JobServiceModel(job));
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
    public CompletionStage<JobServiceModel> getJobAsync(String jobId)
        throws ExternalDependencyException {
        try {
            JobResult result = this.jobClient.getJob(jobId);
            JobServiceModel jobModel = new JobServiceModel(result);
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
            JobServiceModel jobModel = new JobServiceModel(result);
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
            JobResult result = this.jobClient.scheduleUpdateTwin(
                jobId,
                queryCondition,
                twin.toDeviceTwinDevice(),
                startTime,
                maxExecutionTimeInSeconds);
            JobServiceModel jobModel = new JobServiceModel(result);
            return CompletableFuture.supplyAsync(() -> jobModel);
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to schedule twin update job: %s, %s, %s",
                jobId, queryCondition, Json.stringify(Json.toJson(twin)));
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    // As a work around to support empty jobType and jobStatus
    // we will leverage current SDK query function to retrieve
    // jobs with type of scheduleDeviceMethod and scheduleUpdateTwin
    // and status of queued, running, failed, completed.
    // https://github.com/Azure/iothub-manager-java/issues/48
    private CompletionStage<List<JobServiceModel>> getJobsWorkaroundAsync(JobType type, JobStatus status, Integer size, long from, long to)
            throws InvalidInputException, ExternalDependencyException {
        List<JobType> types = new ArrayList();
        List<JobStatus> statuses = new ArrayList();
        try {
            if (type == null) {
                types.add(JobType.scheduleDeviceMethod);
                // uncomment this line once issue 26 is fixed:
                // https://github.com/Azure/iothub-manager-java/issues/26
                // types.add(JobType.scheduleUpdateTwin);

                // uncomment this line once issue 27 is fixed:
                // https://github.com/Azure/iothub-manager-java/issues/27
                // types.add(JobType.unknown);
            } else {
                types.add(type);
            }
            if (status == null) {
                statuses.add(JobStatus.running);
                statuses.add(JobStatus.failed);
                statuses.add(JobStatus.completed);
                statuses.add(JobStatus.queued);
            } else {
                statuses.add(status);
            }
        } catch (IllegalArgumentException e) {
            log.error(String.format("Invalid query string: %s, %s, %s", type, status, size));
            throw new InvalidInputException(String.format("Invalid query string: %s, %s, %s", type, status, size), e);
        }

        ArrayList<CompletableFuture<List<JobServiceModel>>> queries = new ArrayList<>();
        try {
            for (JobType t : types) {
                for (JobStatus s : statuses) {
                    Query query = this.jobClient.queryJobResponse(
                        JobType.toAzureJobType(t),
                        JobStatus.toAzureJobStatus(s),
                        size);
                    List jobs = new ArrayList<JobServiceModel>();
                    while (this.jobClient.hasNextJob(query)) {
                        JobResult job = this.jobClient.getNextJob(query);
                        if (job.getCreatedTime().getTime() >= from && job.getCreatedTime().getTime() <= to) {
                            jobs.add(new JobServiceModel(job));
                        }
                    }
                    queries.add(CompletableFuture.supplyAsync(() -> jobs));
                }
            }
        } catch (IotHubException | IOException e) {
            String message = String.format("Unable to query device jobs by: %s, %s, %d", type, status, size);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(queries.toArray(new CompletableFuture[queries.size()]));
        CompletableFuture<List<List<JobServiceModel>>> allResults = allFutures.
            thenApply(r -> queries.stream().map(future -> future.join()).collect(Collectors.toList()));
        return allResults.thenApply(
            result -> {
                List jobList = new ArrayList<JobServiceModel>();
                result.forEach(jobs -> jobs.forEach(job -> jobList.add(job)));
                return jobList;
            }
        );
    }
}
