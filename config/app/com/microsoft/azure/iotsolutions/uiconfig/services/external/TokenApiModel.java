// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class TokenApiModel {
    private String audience;
    private String accessTokenType;
    private String accessToken;
    private String authority;
    private DateTime expiresOn;

    @JsonProperty("Audience")
    public String getAudience() {
        return this.audience;
    }

    @JsonProperty("AccessTokenType")
    public String getAccessTokenType() {
        return this.accessTokenType;
    }

    @JsonProperty("AccessToken")
    public String getAccessToken() {
        return this.accessToken;
    }

    @JsonProperty("Authority")
    public String getAuthority() {
        return this.authority;
    }

    @JsonProperty("ExpiresOn")
    public DateTime getExpiresOn() {
        return this.expiresOn;
    }
}
