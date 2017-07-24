// Copyright (c) Microsoft. All rights reserved.

package webservice.test.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.Config;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.core.Is.is;
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

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void providesWebServicePort() {
        Config target = new Config();
        int port = target.getPort();
        assertThat(port, is(9004));
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void providesDocDbConnectionString() {
        Config target = new Config();
        String connectionString = target.getServicesConfig().getStorageConnectionString();
        assertNotNull(connectionString);
    }
}
