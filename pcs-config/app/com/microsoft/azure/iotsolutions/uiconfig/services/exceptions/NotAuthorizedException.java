// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.exceptions;

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