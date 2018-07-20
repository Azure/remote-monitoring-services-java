// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.IAlarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.AlarmsController;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmStatus;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.libs.Json.toJson;

public class AlarmsControllerTest {
    private static final Logger.ALogger log = Logger.of(AlarmsControllerTest.class);

    private final String docSchemaKey = "doc.schema";
    private final String docSchemaValue = "alarm";

    private final String docSchemaVersionKey = "doc.schemaVersion";
    private final int docSchemaVersionValue = 1;

    private final String createdKey = "created";
    private final String modifiedKey = "modified";
    private final String descriptionKey = "description";
    private final String statusKey = "status";
    private final String deviceIdKey = "device.id";

    private final String ruleIdKey = "rule.id";
    private final String ruleSeverityKey = "rule.severity";
    private final String ruleDescriptionKey = "rule.description";

    @Before
    public void setUp() {
        // setup before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itGetAlarmById() throws Exception {
        AlarmServiceModel alarmResult = new AlarmServiceModel();

        IAlarms alarms = mock(IAlarms.class);
        AlarmsController controller = new AlarmsController(alarms);
        when(alarms.get(
            "1"))
            .thenReturn(alarmResult);

        // Act
        Result response = controller.get("1");

        // Assert
        assertThat(response.body().isKnownEmpty(), is(false));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itGetAlarms() throws Exception {
        ArrayList<AlarmServiceModel> alarmResult = new ArrayList<AlarmServiceModel>() {{
            add(new AlarmServiceModel());
            add(new AlarmServiceModel());
        }};

        IAlarms alarms = mock(IAlarms.class);
        AlarmsController controller = new AlarmsController(alarms);
        when(alarms.getList(
            DateTime.now(), DateTime.now(), "asc", 0, 100, new String[0]))
            .thenReturn(alarmResult);

        // Act
        Result response = controller.list(null, null, null, 0, 0, null);

        // Assert
        assertThat(response.body().isKnownEmpty(), is(false));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itUpdateAlarm() throws Exception {
        AlarmServiceModel alarmResult = new AlarmServiceModel();

        IAlarms alarms = mock(IAlarms.class);
        AlarmsController controller = new AlarmsController(alarms);
        when(alarms.update(
            "1",
            "open"))
            .thenReturn(alarmResult);

        setMockContext();

        // Act
        Result response = controller.patch("1");

        // Assert
        assertThat(response.body().isKnownEmpty(), is(false));
    }

    private void setMockContext() {
        Http.Request mockRequest = mock(Http.Request.class);
        when(mockRequest.body()).thenReturn(new Http.RequestBody(toJson(new AlarmStatus("open"))));

        Http.Context mockContext = mock(Http.Context.class);
        when(mockContext.request()).thenReturn(mockRequest);

        Http.Context.current.set(mockContext);
    }
}
