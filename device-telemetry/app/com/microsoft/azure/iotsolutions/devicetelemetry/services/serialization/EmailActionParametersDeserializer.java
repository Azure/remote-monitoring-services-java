package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class EmailActionParametersDeserializer extends JsonDeserializer<Map<String, Object>> {

    private static final String RECIPIENTS_KEY = "Recipients";

    @Override
    public Map<String, Object> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> parameters = objectMapper.readValue(parser, Map.class);

        Map<String,Object> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        result.putAll(parameters);

        if (result.containsKey(RECIPIENTS_KEY) && result.get(RECIPIENTS_KEY) != null) {
            List<String> recipientsList = Arrays.asList((String[])result.get(RECIPIENTS_KEY));
            result.put(RECIPIENTS_KEY, recipientsList);
        }
        return result;
    }
}
