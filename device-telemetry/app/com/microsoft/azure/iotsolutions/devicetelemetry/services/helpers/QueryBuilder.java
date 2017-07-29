// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers;

import org.joda.time.DateTime;

public class QueryBuilder {
    public static String buildSQL(
        String schemaName,
        String byId,
        String byIdPropertyName,
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

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT TOP " + (skip + limit) + " * FROM c WHERE (c[`doc.schema`] = `" + schemaName + "`");
        if (devices.length > 0) {
            String ids = String.join("`,`", devices);
            queryBuilder.append(" AND c[`" + devicesProperty + "`] IN (`" + ids + "`)");
        }

        if(byId != null) {
            queryBuilder.append(" AND c[`" + byIdPropertyName + "`] = `" + byId + "`");
        }

        if (from != null) {
            queryBuilder.append(" AND c[`" + fromProperty + "`] >= `" + from.toDateTime().getMillis() + "`");
        }
        if (to != null) {
            queryBuilder.append(" AND c[`" + toProperty + "`] <= `" + to.toDateTime().getMillis() + "`");
        }
        queryBuilder.append(")");

        if (order.equalsIgnoreCase("desc")) {
            queryBuilder.append(" ORDER BY c[`" + orderProperty + "`] DESC");
        } else {
            queryBuilder.append(" ORDER BY c[`" + orderProperty + "`] ASC");
        }

        return queryBuilder.toString().replace('`', '"');
    }
}
