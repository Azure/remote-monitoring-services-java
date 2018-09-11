package services.test.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import helpers.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class TimeSeriesConfigTest {

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenFqdnIsNull() throws Throwable {
        new TimeSeriesConfig("", "", "", "", "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenFqdnIsEmpty() throws Throwable {
        new TimeSeriesConfig("", "", "", "", "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAadTenantIsNull() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", null, "", "", "", "", "", "", "",20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAadTenantIsEmpty() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "", "", "", "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAppIdIsNull() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", null, "", "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAppIdIsEmpty() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "", "", "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAppSecretIsNull() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", null, "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenAppSecretIsEmpty() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", "", "", "", "", "", "", 20);
    }

    @Test(timeout = 10000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenApiVersionIsNull() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", "secret", null, "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenApiVersionIsEmpty() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", "secret", "", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenDateFormatIsNull() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", "secret", "2016-12-12", "", "", "", null, 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itThrowsInvalidConfiguration_WhenDateFormatIsEmpty() throws Throwable {
        new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", "secret", "2016-12-12", "", "", "", "", 20);
    }

    @Test(timeout = 1000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void itSucceeds_WhenDefaultValuesAreUsed() throws Throwable {
        TimeSeriesConfig config = new TimeSeriesConfig("abc.env.timeseries.azure.com", "tenant", "appid", "secret", "2016-12-12", "", "", "", "", 20);
        Assert.assertEquals("https://login.windows.net/", config.getAuthorityUrl());
        Assert.assertEquals("https://api.timeseries.azure.com/", config.getAudienceUrl());
        Assert.assertEquals("https://insights.timeseries.azure.com/", config.getExplorerUrl());
    }
}
