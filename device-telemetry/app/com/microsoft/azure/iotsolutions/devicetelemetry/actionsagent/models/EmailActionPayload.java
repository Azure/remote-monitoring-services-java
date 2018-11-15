// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EmailActionPayload {

    private String subject;
    private String body;
    private List<String> recipients;

    public EmailActionPayload(List<String> recipients, String subject, String body){
        this.subject = subject;
        this.body = body;
        this.recipients = recipients;
    }

    @JsonProperty("subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("recipients")
    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}
