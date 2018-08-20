// Copyright (c) Microsoft. All rights reserved.

package services.test.storage.timeSeries;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.EventListApiModel;
import helpers.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.libs.Json;

import java.io.InputStream;

public class EventListApiModelTest {

    private final String TSI_SAMPLE_EVENTS_FILE = "TimeSeriesEvents.json";
    private EventListApiModel events;

    @Before
    public void setUp() {
        InputStream stream = EventListApiModelTest.class.getResourceAsStream(TSI_SAMPLE_EVENTS_FILE);
        this.events = Json.fromJson(Json.parse(stream), EventListApiModel.class);
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itConvertsToMessageList_WhenMultipleDeviceTypes() throws Throwable {
        // Act
        MessageListServiceModel result = events.toMessageList(0);

        // Assert
        Assert.assertEquals(4, result.getMessages().size());
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itConvertsToMessageList_WithSkipValue() throws Throwable {
        // Act
        MessageListServiceModel result = events.toMessageList(2);

        // Assert
        Assert.assertEquals(2, result.getMessages().size());
    }
}
