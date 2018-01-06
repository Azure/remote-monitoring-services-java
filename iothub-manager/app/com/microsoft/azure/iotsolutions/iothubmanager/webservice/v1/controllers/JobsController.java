// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IJobs;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.helpers.DateHelper;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.JobApiModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import javax.transaction.NotSupportedException;
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
        throws InvalidInputException, ExternalDependencyException {
        String type = request().getQueryString("jobType");
        String status = request().getQueryString("jobStatus");
        String size = request().getQueryString("pageSize");
        String from = request().getQueryString("from");
        String to = request().getQueryString("to");
        JobType jobType;
        JobStatus jobStatus;
        long jobFrom;
        long jobTo;
        Integer pageSize;
        try {
            DateTime temp;
            jobType = type == null || type.isEmpty() ? null : JobType.from(Integer.parseInt(type));
            jobStatus = status == null || status.isEmpty() ? null : JobStatus.from(Integer.parseInt(status));
            from = (from == null || from.isEmpty()) ? "" : from;
            to = (to == null || to.isEmpty()) ? "" : to;
            pageSize = size == null || size.isEmpty() ? 100 : Integer.parseInt(size);

            temp = DateHelper.parseDate(from);
            jobFrom = (temp == null) ? Long.MIN_VALUE : temp.getMillis();
            temp = DateHelper.parseDate(to);
            jobTo = (temp == null) ? Long.MAX_VALUE : temp.getMillis();
        } catch (IllegalArgumentException e) {
            String message = String.format("Invalid query string: %s, %s, %s", type, status, size);
            log.error(message, e);
            throw new InvalidInputException(message, e);
        }

        return this.jobService.getJobsAsync(jobType, jobStatus, pageSize, jobFrom, jobTo)
            .thenApply(jobs -> {
                List jobList = new ArrayList<JobApiModel>();
                jobs.forEach(job -> jobList.add(new JobApiModel(job)));
                return ok(toJson(jobList));
            });
    }

    public CompletionStage<Result> getJobAsync(String jobId)
        throws InvalidInputException, ExternalDependencyException {
        String includeDeviceDetails = request().getQueryString("includeDeviceDetails");
        String deviceJobStatus = request().getQueryString("deviceJobStatus");
        Boolean include;
        DeviceJobStatus status;
        try {
            include = Boolean.parseBoolean(includeDeviceDetails);
            status = deviceJobStatus == null ? null : DeviceJobStatus.from(Integer.parseInt(deviceJobStatus));
        } catch (IllegalArgumentException e) {
            String message = String.format("Invalid query string: %s, %s", includeDeviceDetails, deviceJobStatus);
            log.error(message, e);
            throw new InvalidInputException(message, e);
        }
        return this.jobService.getJobAsync(jobId, include, status)
            .thenApply(job -> ok(toJson(new JobApiModel(job))));
    }

    public CompletionStage<Result> scheduleJobAsync()
        throws NotSupportedException, ExternalDependencyException {
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
