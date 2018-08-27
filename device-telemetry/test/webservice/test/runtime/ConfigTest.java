// Copyright (c) Microsoft. All rights reserved.

package webservice.test.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.AlarmsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.Config;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

public class ConfigTest {

    @Before
    public void setUp() {
        // something before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void providesCosmosDbConnectionString() throws InvalidConfigurationException {
        Config target = new Config();
        AlarmsConfig alarmsConfig = target.getServicesConfig().getAlarmsConfig();
        String connectionString = alarmsConfig.getStorageConfig().getDocumentDbConnString();
        assertNotNull(connectionString);
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void provideKeyValueWebserviceUrl() throws InvalidConfigurationException {
        Config target = new Config();
        String url = target.getServicesConfig().getKeyValueStorageUrl();
    }
}
