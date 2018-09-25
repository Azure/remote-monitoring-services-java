// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceGroupConditionApiModel {

    private OperatorType operator;
    private Object value;
    private String key;

    @JsonProperty("Key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("Operator")
    public OperatorType getOperator() {
        return operator;
    }

    public void setOperator(OperatorType operator) {
        this.operator = operator;
    }

    @JsonProperty("Value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}