// Copyright (c) Microsoft. All rights reserved.

package webservice.test.runtime;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.Config;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.libs.ws.WSClient;

import static org.junit.Assert.assertNotNull;

public class ConfigTest {
    private WSClient client;

    @Before @Inject
    public void setUp(WSClient client) {
        this.client = client;
        // something before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void providesDocDbConnectionString() {
        Config target = new Config(this.client);
        String connectionString = target.getServicesConfig().getStorageConnectionString();
        assertNotNull(connectionString);
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void provideKeyValueWebserviceUrl() {
        Config target = new Config(this.client);
        String url = target.getServicesConfig().getKeyValueStorageUrl();
    }
}
