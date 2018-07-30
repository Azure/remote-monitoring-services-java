// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryConditionClause {

    private String key;
    private String operator;
    private Object value;
    private Boolean textual;

    public QueryConditionClause(final String key, final String operator, final Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
        this.textual = true;
    }

    @JsonProperty("Key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("Operator")
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @JsonProperty("Value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean isTextual() {
        return this.textual;
    }

    public void setTextual(Boolean textual) {
        this.textual = textual;
    }
}
