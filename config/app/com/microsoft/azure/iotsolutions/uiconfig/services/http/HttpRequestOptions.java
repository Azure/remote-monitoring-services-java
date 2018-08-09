// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.http;


public class HttpRequestOptions {
    private boolean ensureSuccess = false;
    private boolean allowInsecureSSLServer = false;
    private int timeout = 30000;

    public boolean isEnsureSuccess() {
        return ensureSuccess;
    }

    public void setEnsureSuccess(boolean ensureSuccess) {
        this.ensureSuccess = ensureSuccess;
    }

    public boolean isAllowInsecureSSLServer() {
        return allowInsecureSSLServer;
    }

    public void setAllowInsecureSSLServer(boolean allowInsecureSSLServer) {
        this.allowInsecureSSLServer = allowInsecureSSLServer;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
