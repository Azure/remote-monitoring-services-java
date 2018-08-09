// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import org.junit.After;
import org.junit.Before;

public class ConfigTest {

    private Config config = null;

    @Before
    public void setUp() throws InvalidConfigurationException {
        config = new Config();
    }

    @After
    public void tearDown() {
        // something after every test
    }
}
