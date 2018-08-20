// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SchemaModel {

    private long rid;
    private String eventSourceName;
    private List<PropertyModel> properties;

    private final String DEVICE_ID_KEY = "iothub-connection-device-id";

    private final HashSet<String> excludeProperties = new HashSet<String>() {{
        add("content-type");
        add("content-encoding");
        add("$$CreationTimeUtc");
        add("$$MessageSchema");
        add("$$ContentType");
        add("iothub-creation-time-utc");
        add("iothub-connection-device-id");
        add("iothub-connection-auth-method");
        add("iothub-connection-auth-generation-id");
        add("iothub-enqueuedtime");
        add("iothub-message-schema");
        add("iothub-message-source");
    }};

    @JsonProperty("rid")
    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    @JsonProperty("$esn")
    public String getEventSourceName() {
        return eventSourceName;
    }

    public void setEventSourceName(String eventSourceName) {
        this.eventSourceName = eventSourceName;
    }

    @JsonProperty("properties")
    public List<PropertyModel> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyModel> properties) {
        this.properties = properties;
    }

    public HashMap<String, Integer> getPropertiesByIndex() {
        HashMap<String, Integer> result = new HashMap<>();

        for (int i = 0; i < this.properties.size(); i++) {
            PropertyModel property = this.properties.get(i);
            if (!this.excludeProperties.contains(property.getName())) {
                result.put(property.getName(), i);
            }
        }
        
        return result;
    }

    public int getDeviceIdIndex() throws InvalidInputException {
        for (int i = 0; i < this.properties.size(); i++) {
            if (this.properties.get(i).getName().equalsIgnoreCase(DEVICE_ID_KEY)) {
                return i;
            }
        }

        throw new InvalidInputException(String.format(
            "No device id found in message schema from Time Series Insights. " +
                "Device id property '%s' is missing.", DEVICE_ID_KEY));
    }
}
