// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IJobs;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.JobStatus;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.JobType;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.JobApiModel;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import javax.transaction.NotSupportedException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

public final class JobsController extends Controller {

    private static final Logger.ALogger log = Logger.of(JobsController.class);

    private final IJobs jobService;

    @Inject
    public JobsController(final IJobs jobService) {
        this.jobService = jobService;
    }

    public CompletionStage<Result> getJobsAsync()
        throws BaseException {
        String type = request().getQueryString("jobType");
        String status = request().getQueryString("jobStatus");
        String size = request().getQueryString("pageSize");
        JobType jobType;
        JobStatus jobStatus;
        Integer pageSize;

        if (type == null || type.isEmpty()) {
            log.error("jobType is null or empty in query string");
            throw new InvalidInputException("jobType is null or empty in query string");
        }

        if (status == null || status.isEmpty()) {
            log.error("jobStatus is null or empty in query string");
            throw new InvalidInputException("jobStatus is null or empty in query string");
        }

        try {
            jobType = JobType.from(Integer.parseInt(type));
            jobStatus = JobStatus.from(Integer.parseInt(status));
            pageSize = Integer.parseInt(size);
        } catch (IllegalArgumentException e) {
            log.error(String.format("Invalid query string: %s, %s, %s", type, status, size));
            throw new InvalidInputException(String.format("Invalid query string: %s, %s, %s", type, status, size), e);
        }

        return this.jobService.getJobsAsync(jobType, jobStatus, pageSize)
            .thenApply(jobs -> {
                List jobApiModels = new ArrayList<JobApiModel>();
                jobs.forEach(job -> jobApiModels.add(new JobApiModel(job)));
                return ok(toJson(jobs));
            });
    }

    public CompletionStage<Result> getJobAsync(String jobId)
        throws IOException, IotHubException, BaseException {
        return this.jobService.getJobAsync(jobId)
            .thenApply(job -> ok(toJson(new JobApiModel(job))));
    }

    public CompletionStage<Result> scheduleJobAsync()
        throws BaseException, NotSupportedException {
        JsonNode json = request().body().asJson();
        final JobApiModel jobApiModel = fromJson(json, JobApiModel.class);

        if (jobApiModel.getUpdateTwin() != null) {
            return jobService.scheduleTwinUpdateAsync(
                jobApiModel.getJobId(),
                jobApiModel.getQueryCondition(),
                jobApiModel.getUpdateTwin(),
                jobApiModel.getStartTimeUtc() == null ?
                    DateTime.now(DateTimeZone.UTC).toDate() : jobApiModel.getStartTimeUtc(),
                jobApiModel.getMaxExecutionTimeInSeconds() == null ?
                    3600 : jobApiModel.getMaxExecutionTimeInSeconds())
                .thenApply(job -> ok(toJson(new JobApiModel(job))));
        }

        if (jobApiModel.getMethodParameter() != null) {
            return jobService.scheduleDeviceMethodAsync(
                jobApiModel.getJobId(),
                jobApiModel.getQueryCondition(),
                jobApiModel.getMethodParameter().toServiceModel(),
                jobApiModel.getStartTimeUtc() == null ?
                    DateTime.now(DateTimeZone.UTC).toDate() : jobApiModel.getStartTimeUtc(),
                jobApiModel.getMaxExecutionTimeInSeconds() == null ?
                    3600 : jobApiModel.getMaxExecutionTimeInSeconds())
                .thenApply(job -> ok(toJson(new JobApiModel(job))));
        }

        throw new NotSupportedException();
    }
}
