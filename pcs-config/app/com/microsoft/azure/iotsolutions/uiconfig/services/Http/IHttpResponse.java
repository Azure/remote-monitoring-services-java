// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.Http;

import org.apache.http.Header;

public interface IHttpResponse {

    int getStatusCode();

    Header[] getHeaders();

    String getContent();

    boolean getIsRetriableError();

    default boolean isSuccessStatusCode() {
        return (getStatusCode() >= 200) && (getStatusCode() <= 299);
    }
}
