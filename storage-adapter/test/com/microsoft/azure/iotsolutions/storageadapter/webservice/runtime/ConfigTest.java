// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime;

import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    @Before
    public void setUp() {
        // something before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    // IMPORTANT: when creating a service from the template, uncomment this test
    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void providesWebServicePort() {
        Config config = new Config();
        assertThat(config.getPort(), not(0));
        assertNotNull(config.getServicesConfig().getContainerName());
        assertNotNull(config.getServicesConfig().getDocumentDBConnectionString());
    }
}
