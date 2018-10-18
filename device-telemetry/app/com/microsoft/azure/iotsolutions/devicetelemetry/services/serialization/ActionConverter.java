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
    private static final String TYPE = "Type";
    private static final String PARAMETERS = "Parameters";
    private static final String SUBJECT = "Subject";
    private static final String TEMPLATE = "Template";
    private static final String EMAIL = "Email";
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
                Type = IActionServiceModel.Type.valueOf(node.get(TYPE).asText());
                if (Type == IActionServiceModel.Type.Email) {
                    Subject = node.get(PARAMETERS).has(SUBJECT) ? node.get(PARAMETERS).get(SUBJECT).asText() : "";
                    Body = node.get(PARAMETERS).get(TEMPLATE).asText();
                    Email = new ArrayList<>();
                    if (node.get(PARAMETERS).get(EMAIL).isArray()) {
                        for (final JsonNode subNode : node.get(PARAMETERS).get(EMAIL)) {
                            Email.add(subNode.asText());
                        }
                    }

                    Parameters = new HashMap<>();
                    Parameters.put(SUBJECT, Subject);
                    Parameters.put(TEMPLATE, Body);
                    Parameters.put(EMAIL, Email);

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
