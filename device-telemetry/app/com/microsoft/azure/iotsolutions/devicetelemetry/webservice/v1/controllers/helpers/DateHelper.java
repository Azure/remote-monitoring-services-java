// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.helpers;

import org.joda.time.*;

public class DateHelper {

    public static DateTime parseDate(String text) {

        if (text == null || text.isEmpty()) return null;

        text = text.trim();
        String utext = text.toUpperCase();
        DateTime now = new DateTime(DateTimeZone.UTC);

        if (utext.equals("NOW")) {
            return now;
        }

        if (utext.startsWith("NOW-")) {
            Period delta = Period.parse(utext.substring(4));
            return now.minus(delta);
        }

        // Support the special case of "+" being url decoded to " " in case
        // the client forgot to encode the plus correctly using "%2b"
        if (utext.startsWith("NOW+") || utext.startsWith("NOW ")) {
            Period delta = Period.parse(utext.substring(4));
            return now.plus(delta);
        }

        return DateTime.parse(text);
    }
}
