// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

public class Config implements IConfig {

    private final String Namespace = "com.microsoft.azure.iotsolutions.";
    private final String Application = "devicetelemetry.";

    private com.typesafe.config.Config data;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        this.data = ConfigFactory.load();
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return this.data.getInt(Namespace + Application + "webservice-port");
    }

    /**
     * Get the hostname where the service listen for requests, e.g. 0.0.0.0 when
     * listening to all the network adapters.
     *
     * @return Hostname or IP address
     */
    public String getHostname() {
        return this.data.getString(Namespace + Application + "webservice-hostname");
    }
}
