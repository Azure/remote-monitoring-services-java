// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.UUID;

/**
 * Helper capturing runtime information.
 */
final public class Uptime {

    private static String processId = "WebService." + UUID.randomUUID().toString();

    /**
     * Don't allow instantiation, all fields and methods are static.
     */
    private Uptime() {
    }

    /**
     * @return When the service started.
     */
    public static DateTime getStart() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return new DateTime(runtimeMXBean.getStartTime()).toDateTime(DateTimeZone.UTC);
    }

    /**
     * @return How long the service has been running.
     */
    public static Duration getDuration() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Duration.millis(runtimeMXBean.getUptime());
    }

    /**
     * @return A randomly generated ID used to identify the process in the logs.
     */
    public static String getProcessId() {
        return processId;
    }
}
