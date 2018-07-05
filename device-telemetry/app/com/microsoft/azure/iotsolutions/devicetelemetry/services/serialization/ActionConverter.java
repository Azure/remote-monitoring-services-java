package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.EmailServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.IActionServiceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActionConverter extends JsonDeserializer<ArrayList<IActionServiceModel>> {
    @Override
    public ArrayList<IActionServiceModel> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec oc = parser.getCodec();
        JsonNode arrNode = oc.readTree(parser);

        ArrayList<IActionServiceModel> arr = new ArrayList<>();
        if (arrNode.isArray() && arrNode.hasNonNull(0)) {
            for (final JsonNode node : arrNode) {
                final IActionServiceModel.Type Type = IActionServiceModel.Type.valueOf(node.get("Type").asText());
                if (Type.equals(IActionServiceModel.Type.Email)) {
                    final String Subject = node.get("Parameters").has("Subject") ? node.get("Parameters").get("Subject").asText() : "";
                    final String Body = node.get("Parameters").get("Template").asText();
                    final ArrayList<String> Email = new ArrayList<>();
                    if (node.get("Parameters").get("Email").isArray()) {
                        for (final JsonNode subNode : node.get("Parameters").get("Email")) {
                            Email.add(subNode.asText());
                        }
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("Subject", Subject);
                    map.put("Template", Body);
                    map.put("Email", Email);

                    try {
                        EmailServiceModel model = new EmailServiceModel(Type, map);
                        arr.add(model);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return arr;
    }
}
