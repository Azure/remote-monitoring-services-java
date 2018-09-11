// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class EventApiModel {

    private long schemaRowId;
    private SchemaModel schema;
    private String timestamp;
    private List<JsonNode> values;

    @JsonProperty("schemaRid")
    public long getSchemaRowId() {
        return schemaRowId;
    }

    public void setSchemaRowId(long schemaRowId) {
        this.schemaRowId = schemaRowId;
    }

    @JsonProperty("schema")
    public SchemaModel getSchema() {
        return schema;
    }

    public void setSchema(SchemaModel schema) {
        this.schema = schema;
    }

    @JsonProperty("$ts")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("values")
    public List<JsonNode> getValues() {
        return values;
    }

    public void setValues(List<JsonNode> values) {
        this.values = values;
    }
}
