// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers;

import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import org.joda.time.DateTime;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    private static final Logger.ALogger log = Logger.of(QueryBuilder.class);

    private static final String VALID_CHAR_PATTERN = "[a-zA-Z0-9,.;:_-]*";

    public static SqlQuerySpec getDocumentsSQL(
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
        String devicesProperty) throws InvalidInputException {

        schemaName = validateInput(schemaName);
        fromProperty = validateInput(fromProperty);
        toProperty = validateInput(toProperty);
        orderProperty = validateInput(orderProperty);
        devicesProperty = validateInput(devicesProperty);
        validateInput(String.join(",", devices));

        StringBuilder queryBuilder = new StringBuilder();
        SqlParameterCollection sqlParameterCollection = new SqlParameterCollection();

        queryBuilder.append("SELECT TOP @top * FROM c WHERE (c['doc.schema'] = @schemaName");
        sqlParameterCollection.add(new SqlParameter("@top", skip + limit));
        sqlParameterCollection.add(new SqlParameter("@schemaName", schemaName));

        if (devices.length > 0) {
            SqlParameterCollection devicesParameterCollection = buildSqlParameterCollection("devicesParameterName", devices);
            queryBuilder.append(String.format(" AND c[@devicesProperty] IN (%s)",
                String.join(",", getSqlParameterNames(devicesParameterCollection))));
            sqlParameterCollection.add(new SqlParameter("@devicesProperty", devicesProperty));
            sqlParameterCollection.addAll(devicesParameterCollection);
        }

        if (byId != null && byIdProperty != null && !byIdProperty.isEmpty()) {
            byId = validateInput(byId);
            byIdProperty = validateInput(byIdProperty);
            queryBuilder.append(" AND c[@byIdProperty] = @byId");
            sqlParameterCollection.add(new SqlParameter("@byIdProperty", byIdProperty));
            sqlParameterCollection.add(new SqlParameter("@byId", byId));
        }

        if (from != null) {
            queryBuilder.append(" AND c[@fromProperty] >= @from");
            sqlParameterCollection.add(new SqlParameter("@fromProperty", fromProperty));
            sqlParameterCollection.add(new SqlParameter("@from", from.toDateTime().getMillis()));
        }

        if (to != null) {
            queryBuilder.append(" AND c[@toProperty] <= @to");
            sqlParameterCollection.add(new SqlParameter("@toProperty", toProperty));
            sqlParameterCollection.add(new SqlParameter("@to", to.toDateTime().getMillis()));
        }

        queryBuilder.append(")");

        if (order == null || order.isEmpty() || order.equalsIgnoreCase("desc")) {
            queryBuilder.append(" ORDER BY c[@orderProperty] DESC");
        } else {
            queryBuilder.append(" ORDER BY c[@orderProperty] ASC");
        }
        sqlParameterCollection.add(new SqlParameter("@orderProperty", orderProperty));

        return new SqlQuerySpec(queryBuilder.toString(), sqlParameterCollection);
    }

    public static SqlQuerySpec getCountSQL(
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

        schemaName = validateInput(schemaName);
        fromProperty = validateInput(fromProperty);
        toProperty = validateInput(toProperty);
        filterProperty = validateInput(filterProperty);
        devicesProperty = validateInput(devicesProperty);
        validateInput(String.join(",", devices));
        validateInput(String.join(",", filterValues));

        StringBuilder queryBuilder = new StringBuilder();
        SqlParameterCollection sqlParameterCollection = new SqlParameterCollection();

        queryBuilder.append("SELECT VALUE COUNT(1) FROM c WHERE (c['doc.schema'] = @schemaName");
        sqlParameterCollection.add(new SqlParameter("@schemaName", schemaName));

        if (devices.length > 0) {
            SqlParameterCollection devicesParameterCollection = buildSqlParameterCollection("devicesParameterName", devices);
            queryBuilder.append(String.format(" AND c[@devicesProperty] IN (%s)",
                String.join(",", getSqlParameterNames(devicesParameterCollection))));
            sqlParameterCollection.add(new SqlParameter("@devicesProperty", devicesProperty));
            sqlParameterCollection.addAll(devicesParameterCollection);
        }

        if (byId != null && byIdProperty != null && !byIdProperty.isEmpty()) {
            byId = validateInput(byId);
            byIdProperty = validateInput(byIdProperty);
            queryBuilder.append(" AND c[@byIdProperty] = @byId");
            sqlParameterCollection.add(new SqlParameter("@byIdProperty", byIdProperty));
            sqlParameterCollection.add(new SqlParameter("@byId", byId));
        }

        if (from != null) {
            queryBuilder.append(" AND c[@fromProperty] >= @from");
            sqlParameterCollection.add(new SqlParameter("@fromProperty", fromProperty));
            sqlParameterCollection.add(new SqlParameter("@from", from.getMillis()));
        }

        if (to != null) {
            queryBuilder.append(" AND c[@toProperty] <= @to");
            sqlParameterCollection.add(new SqlParameter("@toProperty", toProperty));
            sqlParameterCollection.add(new SqlParameter("@to", to.getMillis()));
        }

        if (filterValues.length > 0) {
            SqlParameterCollection filterParameterCollection = buildSqlParameterCollection("filterParameterName", filterValues);
            queryBuilder.append(String.format(" AND c[@filterProperty] IN (%s)",
                String.join(",", getSqlParameterNames(filterParameterCollection))));
            sqlParameterCollection.add(new SqlParameter("@filterProperty", filterProperty));
            sqlParameterCollection.addAll(filterParameterCollection);
        }

        queryBuilder.append(")");

        return new SqlQuerySpec(queryBuilder.toString(), sqlParameterCollection);
    }

    private static String validateInput(String input) throws InvalidInputException {

        // trim string
        input = input.trim();

        // check for invalid characters
        if (!input.matches(VALID_CHAR_PATTERN)) {
            String errorMsg = "Input contains invalid characters. Allowable " +
                "input A-Z a-z 0-9 :;.,_-";
            log.error(errorMsg);
            throw new InvalidInputException(errorMsg);
        }

        return input;
    }

    /**
     * Convert SQL IN clause parameter into a collection of SqlParameter naming by original name
     * and values index because Cosmos DB doesn't natively support string array as one SqlParameter.
     *
     * @return a collection of SqlParameters.
     */
    private static SqlParameterCollection buildSqlParameterCollection(String name, String[] values) {
        SqlParameterCollection sqlParameterCollection = new SqlParameterCollection();
        for (int i = 0; i < values.length; i++) {
            sqlParameterCollection.add(new SqlParameter(String.format("@%s%d", name, i), values[i]));
        }
        return sqlParameterCollection;
    }

    //
    /**
     * Get parameter names from existing SqlParameterCollection instance
     *
     * @return a list of parameter names.
     */
    private static List<String> getSqlParameterNames(SqlParameterCollection collection) {
        List<String> parameterNames = new ArrayList();
        SqlParameter[] parameters = collection.toArray(new SqlParameter[0]);
        for (int i = 0; i < parameters.length; i++) {
            parameterNames.add(parameters[i].getName());
        }
        return parameterNames;
    }
}
