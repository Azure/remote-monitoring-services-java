// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel.ActionType;
import play.Logger;

import java.io.IOException;
import java.util.*;

public class ActionDeserializer extends JsonDeserializer<IActionServiceModel> {
    private static final String TYPE = "Type";
    private static final String PARAMETERS = "Parameters";
    private static final String RECIPIENTS_KEY = "Recipients";


    private static final Logger.ALogger log = Logger.of(ActionDeserializer.class);

    @Override
    public IActionServiceModel deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = parser.getCodec();
        JsonNode node = oc.readTree(parser);
        ActionType type = ActionType.valueOf(node.get(TYPE).asText());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parameters = objectMapper.readValue(node.get(PARAMETERS).toString(), Map.class);

        switch (type) {
            // If more action types are added, this switch will grow
            case Email:
                parameters = this.deserializeEmailParameters(parameters);
                try {
                    return new EmailActionServiceModel(parameters);
                } catch (InvalidInputException exception) {
                    this.log.error("Cannot deserialize email action parameters");
                }
                break;
        }

        // If could not deserialize, return null
        return null;
    }

    // Deserialize given parameters dictionary into email parameters.
    private Map<String, Object> deserializeEmailParameters(Map<String, Object> parameters) {
        Map<String,Object> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        result.putAll(parameters);

        if (result.containsKey(RECIPIENTS_KEY) && result.get(RECIPIENTS_KEY) != null) {
            List<String> recipientsList = (ArrayList<String>)result.get(RECIPIENTS_KEY);
            result.put(RECIPIENTS_KEY, recipientsList);
        }

        return result;
    }
}
