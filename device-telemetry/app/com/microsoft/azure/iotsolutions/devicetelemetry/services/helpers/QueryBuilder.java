// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import org.joda.time.DateTime;
import play.Logger;

public class QueryBuilder {

    private static final Logger.ALogger log = Logger.of(QueryBuilder.class);

    private static final String VALID_CHAR_PATTERN = "[a-zA-Z0-9,.;:_'-]*";

    public static String getDocumentsSQL(
        String schemaName,
        String byId,
        String byIdProperty,
        DateTime from,
        String fromProperty,
        DateTime to,
        String toProperty,
        String order,
        String orderProperty,
        int skip,
        int limit,
        String[] devices,
        String devicesProperty) {

        String deviceIds = String.join("`,`", devices);

        // validate and sanitize input strings
        // TODO https://github.com/Azure/device-telemetry-java/issues/98

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT TOP " + (skip + limit) + " * FROM c WHERE (c[`doc.schema`] = `" + schemaName + "`");
        if (devices.length > 0) {
            queryBuilder.append(" AND c[`" + devicesProperty + "`] IN (`" + deviceIds + "`)");
        }

        if (byId != null) {
            queryBuilder.append(" AND c[`" + byIdProperty + "`] = `" + byId + "`");
        }

        if (from != null) {
            queryBuilder.append(" AND c[`" + fromProperty + "`] >= " + from.toDateTime().getMillis());
        }

        if (to != null) {
            queryBuilder.append(" AND c[`" + toProperty + "`] <= " + to.toDateTime().getMillis());
        }
        queryBuilder.append(")");

        if (order == null) {
            queryBuilder.append(" ORDER BY c[`" + orderProperty + "`] DESC");
        } else {
            if (order.equalsIgnoreCase("desc")) {
                queryBuilder.append(" ORDER BY c[`" + orderProperty + "`] DESC");
            } else {
                queryBuilder.append(" ORDER BY c[`" + orderProperty + "`] ASC");
            }
        }

        return queryBuilder.toString().replace('`', '"');
    }

    public static String getCountSQL(
        String schemaName,
        String byId,
        String byIdProperty,
        DateTime from,
        String fromProperty,
        DateTime to,
        String toProperty,
        String[] devices,
        String devicesProperty,
        String[] filterValues,
        String filterProperty) throws InvalidInputException {

        String deviceIds = String.join("`,`", devices);
        String filter = String.join("`,`", filterValues);

        // validate and sanitize input strings
        // TODO https://github.com/Azure/device-telemetry-java/issues/98
        schemaName = validateInput(schemaName);
        byId = validateInput(byId);
        byIdProperty = validateInput(byIdProperty);
        fromProperty = validateInput(fromProperty);
        toProperty = validateInput(toProperty);
        validateInput(String.join(",", devices));
        devicesProperty = validateInput(devicesProperty);
        validateInput(String.join(",", filterValues));
        filterProperty = validateInput(filterProperty);

        // build query
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT VALUE COUNT(1) FROM c WHERE (c[`doc.schema`] = `" + schemaName + "`");

        if (devices.length > 0) {
            queryBuilder.append(" AND c[`" + devicesProperty + "`] IN (`" + deviceIds + "`)");
        }

        if (byId != null) {
            queryBuilder.append(" AND c[`" + byIdProperty + "`] = `" + byId + "`");
        }

        if (from != null) {
            queryBuilder.append(" AND c[`" + fromProperty + "`] >= " + from.toDateTime().getMillis());
        }

        if (to != null) {
            queryBuilder.append(" AND c[`" + toProperty + "`] <= " + to.toDateTime().getMillis());
        }

        if (filterValues.length > 0) {
            queryBuilder.append(" AND c[`" + filterProperty + "`] IN (`" + filter + "`)");
        }

        queryBuilder.append(")");

        return queryBuilder.toString().replace('`', '"');
    }

    private static String validateInput(String input) throws InvalidInputException {

        // trim string
        input = input.trim();

        // check for invalid characters
        if (!input.matches(VALID_CHAR_PATTERN)) {
            String errorMsg = "input contains invalid characters. Allowable " +
                "input A-Z a-z 0-9 :;.,_-";
            log.error(errorMsg);
            throw new InvalidInputException(errorMsg);
        }

        return input;
    }
}
