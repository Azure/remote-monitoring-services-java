package com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActionConverter extends JsonDeserializer<ArrayList<IAction>> {
    private static final String TYPE = "ActionType";
    private static final String PARAMETERS = "Parameters";
    private static final String SUBJECT = "Subject";
    private static final String NOTES = "Notes";
    private static final String RECIPIENTS = "Recipients";
    private String Subject;
    private String Body;
    private ArrayList<String> Recipients;
    private IAction.ActionType Type;
    private Map<String, Object> Parameters;

    @Override
    public ArrayList<IAction> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec oc = parser.getCodec();
        JsonNode arrNode = oc.readTree(parser);

        ArrayList<IAction> arr = new ArrayList<>();
        if (arrNode.isArray() && arrNode.hasNonNull(0)) {
            for (final JsonNode node : arrNode) {
                Type = IAction.ActionType.valueOf(node.get(TYPE).asText());
                if (Type == IAction.ActionType.Email) {
                    Subject = node.get(PARAMETERS).has(SUBJECT) ? node.get(PARAMETERS).get(SUBJECT).asText() : "";
                    Body = node.get(PARAMETERS).get(NOTES).asText();
                    Recipients = new ArrayList<>();
                    if (node.get(PARAMETERS).get(RECIPIENTS).isArray()) {
                        for (final JsonNode subNode : node.get(PARAMETERS).get(RECIPIENTS)) {
                            Recipients.add(subNode.asText());
                        }
                    }

                    Parameters = new HashMap<>();
                    Parameters.put(SUBJECT, Subject);
                    Parameters.put(NOTES, Body);
                    Parameters.put(RECIPIENTS, Recipients);

                    try {
                        EmailAction model = new EmailAction(Type, Parameters);
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
