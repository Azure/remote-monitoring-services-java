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
    private String Subject;
    private String Body;
    private ArrayList<String> Email;
    private IActionServiceModel.Type Type;
    private Map<String, Object> Parameters;

    @Override
    public ArrayList<IActionServiceModel> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec oc = parser.getCodec();
        JsonNode arrNode = oc.readTree(parser);

        ArrayList<IActionServiceModel> arr = new ArrayList<>();
        if (arrNode.isArray() && arrNode.hasNonNull(0)) {
            for (final JsonNode node : arrNode) {
                Type = IActionServiceModel.Type.valueOf(node.get("Type").asText());
                if (Type == IActionServiceModel.Type.Email) {
                    Subject = node.get("Parameters").has("Subject") ? node.get("Parameters").get("Subject").asText() : "";
                    Body = node.get("Parameters").get("Template").asText();
                    Email = new ArrayList<>();
                    if (node.get("Parameters").get("Email").isArray()) {
                        for (final JsonNode subNode : node.get("Parameters").get("Email")) {
                            Email.add(subNode.asText());
                        }
                    }

                    Parameters = new HashMap<>();
                    Parameters.put("Subject", Subject);
                    Parameters.put("Template", Body);
                    Parameters.put("Email", Email);

                    try {
                        EmailServiceModel model = new EmailServiceModel(Type, Parameters);
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
