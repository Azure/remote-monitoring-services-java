// Copyright (c) Microsoft. All rights reserved.

package services.test.storage.timeSeries;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.ITimeSeriesClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.TimeSeriesClient;
import helpers.UnitTest;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.server.Server;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeSeriesClientTest {

    private WSClient wsClient;
    private IServicesConfig servicesConfig;
    private MessagesConfig messagesConfig;
    private TimeSeriesConfig timeSeriesConfig;
    private ITimeSeriesClient timeSeriesClient;
    private Server server;

    @Before
    public void setUp() throws InvalidConfigurationException {
        this.wsClient = mock(WSClient.class);
        this.timeSeriesConfig = new TimeSeriesConfig(
            String.format("http://localhost:%s/availability", this.server.httpPort()),
            "aadTenant",
            "appId",
            "appSecret",
            "2016-12-12",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            20
        );

        this.messagesConfig = new MessagesConfig(
            "tsi",
            mock(StorageConfig.class),
            this.timeSeriesConfig
        );

        this.servicesConfig = new ServicesConfig(
            "http://localhost:9022/v1",
            mock(MessagesConfig.class),
            mock(AlarmsConfig.class)
        );
    }

    @Test(timeout = 50000)
    @Category({UnitTest.class})
    public void PingReturnsFalse_WhenConfigValuesAreNull() throws Throwable {
        // Arrange
        WSResponse response = mock(WSResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.SC_BAD_REQUEST);

        this.timeSeriesClient = new TimeSeriesClient(this.servicesConfig, this.wsClient);

        // Act & Assert
        Status result = this.timeSeriesClient.ping();

        // Assert
        Assert.assertFalse(result.isHealthy());
    }

    @Test(timeout = 50000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void QueryThrows_IfInvalidAuthParams() throws Throwable {
        // Arrange
        WSResponse response = mock(WSResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.SC_BAD_REQUEST);

        this.timeSeriesClient = new TimeSeriesClient(this.servicesConfig, this.wsClient);

        // Act & Assert
        this.timeSeriesClient.queryEvents(null, null, "desc", 0, 1000, new String[]{"deviceId"});
    }
}
