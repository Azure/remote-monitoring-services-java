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
                QueryConditionClause clause = new QueryConditionClause(
                    node.findValue("Key").asText(),
                    node.findValue("Operator").asText(),
                    node.findValue("Value"));
                clause.setTextual(node.findValue("Value").isTextual());
                clauses.add(clause);
            }
        } catch (Exception e) {
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
}
