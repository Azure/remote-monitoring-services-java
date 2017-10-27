// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;

import java.util.*;
import java.util.concurrent.CompletionStage;

@ImplementedBy(Jobs.class)
public interface IJobs {

    CompletionStage<List<JobServiceModel>> getJobsAsync(
        JobType jobType,
        JobStatus jobStatus,
        Integer pageSize,
        long from, long to)
        throws ExternalDependencyException, InvalidInputException;

    CompletionStage<JobServiceModel> getJobAsync(
        String jobId,
        boolean includeDeviceDetails,
        DeviceJobStatus devicejobStatus)
        throws ExternalDependencyException;

    CompletionStage<JobServiceModel> scheduleTwinUpdateAsync(
        String jobId,
        String queryCondition,
        DeviceTwinServiceModel twin,
        Date startTime,
        long maxExecutionTimeInSeconds)
        throws ExternalDependencyException;

    CompletionStage<JobServiceModel> scheduleDeviceMethodAsync(
        String jobId,
        String queryCondition,
        MethodParameterServiceModel parameter,
        Date startTime,
        long maxExecutionTimeInSeconds)
        throws ExternalDependencyException;
}
