// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.TimeSeriesParseException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;
import org.joda.time.DateTime;

import java.util.*;

public class EventListApiModel {

    private List<EventApiModel> events;

    public EventListApiModel() {

    }

    @JsonProperty("events")
    public List<EventApiModel> getEvents() {
        return this.events;
    }

    public void setEvents(List<EventApiModel> events) {
        this.events = events;
    }

    /**
     * Converts Time Series Events to service message list model.
     * @param skip number of events to skip
     * @return message list service model
     * @throws TimeSeriesParseException
     */
    public MessageListServiceModel toMessageList(int skip) throws TimeSeriesParseException {
        ArrayList<MessageServiceModel> messages = new ArrayList();
        HashSet<String> properties = new HashSet<>();
        HashMap<Long, SchemaModel> schemasMap = new HashMap<>();

        int counter = 0;
        for (EventApiModel event : this.events) {
            // Store schema in map because it is available in the first event and
            // unavailable in following events which will refer the schema id.
            SchemaModel schema = event.getSchema();
            if (schema != null) {
                schemasMap.put(event.getSchema().getRowId(), schema);
            } else {
                schema = schemasMap.get(event.getSchemaRowId());
            }

            MessageServiceModel messageServiceModel;
            try {
                if (counter >= skip) {
                    properties.addAll(schema.getPropertiesByIndex().keySet());
                    messageServiceModel = new MessageServiceModel(
                        event.getValues().get(schema.getDeviceIdIndex()).asText(),
                        DateTime.parse(event.getTimestamp()).getMillis(),
                        this.getEventAsJson(event.getValues(), schema)
                    );
                    messages.add(messageServiceModel);
                }
            } catch (Exception e) {
                throw new TimeSeriesParseException("Failed to parse events from Time Series Insights.", e);
            }
            counter++;
        }

        return new MessageListServiceModel(messages, new ArrayList<>(properties));
    }

    /**
     * Combine the events values and schema model into 'data' JsonNode for message model.
     */
    private JsonNode getEventAsJson(List<JsonNode> values, SchemaModel schema) {
        HashMap<String, Integer> propertiesByIndex = schema.getPropertiesByIndex();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();

        for (Map.Entry<String, Integer> property : propertiesByIndex.entrySet()) {
            result.set(property.getKey(), values.get(property.getValue()));
        }
        return result;
    }
}
