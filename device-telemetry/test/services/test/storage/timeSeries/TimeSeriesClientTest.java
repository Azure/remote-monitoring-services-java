// Copyright (c) Microsoft. All rights reserved.

package services.test.storage.timeSeries;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.ITimeSeriesClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.TimeSeriesClient;
import helpers.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.libs.ws.WSClient;

import java.util.concurrent.CompletionStage;

import static org.mockito.Mockito.mock;

public class TimeSeriesClientTest {

    private WSClient wsClient;
    private IServicesConfig servicesConfig;
    private ITimeSeriesClient timeSeriesClient;

    @Before
    public void setUp() throws InvalidConfigurationException {
        this.wsClient = mock(WSClient.class);
        this.servicesConfig = new ServicesConfig(
            "http://localhost:9022/v1",
            "http://localhost:9001/v1",
            mock(MessagesConfig.class),
            mock(AlarmsConfig.class),
            mock(DiagnosticsConfig.class)
        );
        this.timeSeriesClient = new TimeSeriesClient(this.servicesConfig, this.wsClient);
    }

    @Test(timeout = 50000)
    @Category({UnitTest.class})
    public void PingReturnsFalse_WhenConfigValuesAreNull() throws Throwable {
        // Act & Assert
        StatusResultServiceModel result = this.timeSeriesClient.pingAsync().toCompletableFuture().get();

        // Assert
        Assert.assertFalse(result.getIsHealthy());
    }

    @Test(timeout = 50000, expected = InvalidConfigurationException.class)
    @Category({UnitTest.class})
    public void QueryThrows_IfInvalidAuthParams() throws Throwable {
        // Act & Assert
        this.timeSeriesClient.queryEvents(null, null, "desc", 0, 1000, new String[]{"deviceId"});
    }
}
