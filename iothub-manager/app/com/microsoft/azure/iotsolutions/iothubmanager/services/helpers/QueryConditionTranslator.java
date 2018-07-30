// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;

import java.io.StringWriter;
import java.util.*;

public class QueryConditionTranslator {

    private static final HashMap<String, String> operatorMap = new HashMap<String, String>() {
        {
            put("EQ", "=");
            put("NE", "!=");
            put("LT", "<");
            put("LE", ">=");
            put("GT", ">");
            put("GE", ">=");
            put("IN", "IN");
        }
    };

    public static String ToQueryString(String conditions) throws InvalidInputException {
        List<QueryConditionClause> clauses = new ArrayList<QueryConditionClause>();
        JsonNode jsonResult;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonResult = mapper.readTree(conditions);
            for (JsonNode node : jsonResult) {
                // To make "Key", "Operator" and "Value" case insensitive
                JsonNode keyNode = getNode(node, "Key", "key");
                JsonNode operatorNode = getNode(node, "Operator", "operator");
                JsonNode valueNode = getNode(node, "Value", "value");
                if (keyNode == null || operatorNode == null || valueNode == null) {
                    break;
                }
                QueryConditionClause clause = new QueryConditionClause(
                    keyNode.asText(),
                    operatorNode.asText(),
                    valueNode);
                clause.setTextual(valueNode.isTextual());
                clauses.add(clause);
            }
        } catch (Exception e) {
            // Any exception raised in deserializing will be ignored
        }

        if (clauses.size() == 0) {
            return conditions.replace('\"', '\'').replaceAll("\\[\\s*\\]", "");
        } else {
            StringWriter sb = new StringWriter();
            for (QueryConditionClause clause : clauses) {
                String op = operatorMap.get(clause.getOperator());
                if (op == null) {
                    throw new InvalidInputException("Operator is not valid: " + clause.getOperator());
                }
                String value = clause.getValue().toString();
                String quotedValue;
                if (clause.isTextual()) {
                    quotedValue = value.replace('\"', '\'');
                } else {
                    quotedValue = value;
                }
                sb.append(" and ").append(clause.getKey())
                    .append(' ').append(op).append(' ')
                    .append(quotedValue);
            }
            return sb.toString().replaceFirst(" and ", "");
        }
    }

    private static JsonNode getNode(JsonNode node, String key1, String key2) {
        return node.findValue(key1) != null ? node.findValue(key1) : node.findValue(key2) != null ? node.findValue(key2) : null;
    }
}
