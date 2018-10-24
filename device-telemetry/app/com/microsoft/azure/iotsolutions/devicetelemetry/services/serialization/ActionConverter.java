package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;
import play.libs.Json;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;


public class ActionConverter extends JsonDeserializer<IAction> {

    private static final String TYPE_NODE_NAME = "Type";
    private static final String PARAMETERS_NODE_NAME = "Parameters";

    @Override
    public IAction deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        IAction action = new EmailAction();
        ActionType actionType = ActionType.from(rootNode.get(TYPE_NODE_NAME).asText());
        switch(actionType) {
            case Email:
                HashMap parameters = Json.fromJson(rootNode.get(PARAMETERS_NODE_NAME), HashMap.class);
                TreeMap caseInsensitiveParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                caseInsensitiveParameters.putAll(parameters);
                action.setParameters(caseInsensitiveParameters);
                break;
        }
        return action;
    }
}
