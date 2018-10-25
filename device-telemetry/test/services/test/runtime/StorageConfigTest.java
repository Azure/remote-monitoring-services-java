// Copyright (c) Microsoft. All rights reserved.

package services.test.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import helpers.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class StorageConfigTest {

    @Before
    public void setUp() {
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void itIsValidConfig() throws InvalidConfigurationException {
        StorageConfig config = new StorageConfig(
            "AccountEndpoint=https://xxx.documents.azure.com:443/;AccountKey=abcdefg==;",
            "dbname",
            "collectionName"
        );

        Assert.assertEquals("https://xxx.documents.azure.com:443/", config.getCosmosDbUri());
        Assert.assertEquals("abcdefg==", config.getCosmosDbKey());
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void itIsValidConfig_WhenAccountKeyIsFirst() throws InvalidConfigurationException {
        StorageConfig config = new StorageConfig(
            "AccountKey=abcdefg==;AccountEndpoint=https://xxx.documents.azure.com:443/;",
            "dbname",
            "collectionName"
        );

        Assert.assertEquals("https://xxx.documents.azure.com:443/", config.getCosmosDbUri());
        Assert.assertEquals("abcdefg==", config.getCosmosDbKey());
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void itIsValidConfig_WhenExtraInfoAppended() throws InvalidConfigurationException {
        StorageConfig config = new StorageConfig(
            "AccountEndpoint=https://xxx.documents.azure.com:443/;AccountKey=abcdefg==;ExtraInfo=bla;",
            "dbname",
            "collectionName"
        );

        Assert.assertEquals("https://xxx.documents.azure.com:443/", config.getCosmosDbUri());
        Assert.assertEquals("abcdefg==", config.getCosmosDbKey());
    }

    @Test(timeout = 10000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenKeyAccountEndpointIsNull() throws InvalidConfigurationException {
        StorageConfig config = new StorageConfig(
            "AccountKey=abcdefg==;",
            "dbname",
            "collectionName"
        );

        config.getCosmosDbUri();
    }

    @Test(timeout = 10000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAccountKeyIsNull() throws InvalidConfigurationException {
        StorageConfig config = new StorageConfig(
            "AccountEndpoint=https://xxx.documents.azure.com:443/",
            "dbname",
            "collectionName"
        );

        config.getCosmosDbKey();
    }

    @Test(timeout = 10000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenFormatIsWrong() throws InvalidConfigurationException {
        StorageConfig config = new StorageConfig(
            "https://xxx.documents.azure.com:443/",
            "dbname",
            "collectionName"
        );

        config.getCosmosDbKey();
    }
}
