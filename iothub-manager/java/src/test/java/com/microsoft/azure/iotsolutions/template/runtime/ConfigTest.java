// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.runtime;

import com.microsoft.azure.iotsolutions.template.helpers.FastTests;
import com.microsoft.azure.iotsolutions.template.helpers.UnitTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.core.Is.is;
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

    @Test(timeout = 1000)
    @Category({UnitTests.class, FastTests.class})
    public void providesWebServicePort() {
        Config target = new Config();

        int port = target.getWebServicePort();
        assertThat(port, is(8182));
    }

    @Test(timeout = 1000)
    @Category({UnitTests.class, FastTests.class})
    public void providesWebServiceHostname() {
        Config target = new Config();

        String hostname = target.getHostname();
        assertThat(hostname, is("1.2.3.4"));
    }
}
