// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public class JsonHelper {

    /**
     * returns the object associated with the given key, ignoring case
     * if object cannot be found, returns null
     *
     * @param node
     * @param key
     * @return value for given key
     */
    public static JsonNode getNode(JsonNode node, String key) {

        Iterator<String> fieldNames = node.fieldNames();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.equalsIgnoreCase(key)) {
                return node.get(fieldName);
            }
        }

        return null;
    }
}
