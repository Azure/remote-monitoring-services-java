// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class EventApiModel {

    private long schemaRid;
    private SchemaModel schema;
    private String timestamp;
    private List<JsonNode> values;

    @JsonProperty("schemaRid")
    public long getSchemaRid() {
        return schemaRid;
    }

    public void setSchemaRid(long schemaRid) {
        this.schemaRid = schemaRid;
    }

    @JsonProperty("schema")
    public SchemaModel getSchema() {
        return schema;
    }

    public void setSchema(SchemaModel schema) {
        this.schema = schema;
    }

    @JsonProperty("$ts")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
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
