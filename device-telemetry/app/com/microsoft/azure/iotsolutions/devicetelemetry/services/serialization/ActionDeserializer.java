// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import play.Logger;
import play.libs.Json;
import play.shaded.ahc.io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.util.HashMap;

public class ActionDeserializer extends JsonDeserializer<IActionServiceModel> {

    private static final Logger.ALogger log = Logger.of(ActionDeserializer.class);

    private static final String TYPE_NODE_NAME = "Type";
    private static final String PARAMETERS_NODE_NAME = "Parameters";

    /**
     * Deserialize JSON node based on ActionType
     *
     * @return deserialize value if ActionType is supported, otherwise return null value.
     * @throws IOException
     */
    @Override
    public IActionServiceModel deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        JsonNode typeNode = rootNode.get(TYPE_NODE_NAME);
        if (typeNode == null || StringUtil.isNullOrEmpty(typeNode.asText())) return null;
        ActionType actionType = ActionType.from(typeNode.asText());

        IActionServiceModel action = null;
        switch (actionType) {
            case Email:
                HashMap parameters = Json.fromJson(rootNode.get(PARAMETERS_NODE_NAME), HashMap.class);
                try {
                    action = new EmailActionServiceModel(parameters);
                } catch (InvalidInputException e) {
                    this.log.error("Cannot deserialize email action parameters");
                }
                break;
            default:
                this.log.error("Unknown action type: " + actionType.toString());
        }

        return action;
    }
}
