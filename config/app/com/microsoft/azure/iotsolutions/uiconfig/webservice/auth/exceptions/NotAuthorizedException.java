// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.exceptions;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;

public class NotAuthorizedException extends BaseException {

    public NotAuthorizedException(){
    }

    public NotAuthorizedException(String message) {
        super(message);
    }
    
    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}