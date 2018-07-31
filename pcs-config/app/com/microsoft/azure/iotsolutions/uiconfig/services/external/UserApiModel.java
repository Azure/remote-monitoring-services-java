// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserApiModel {

    private String id;
    private String email;
    private String name;
    private List<String> allowedActions;

    @JsonProperty("Id")
    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    @JsonProperty("Email")
    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    @JsonProperty("Name")
    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    @JsonProperty("AllowedActions")
    public List<String> getAllowedActions() { return this.allowedActions; }

    public void setAllowedActions(List<String> allowedActions) { this.allowedActions = allowedActions; }
}