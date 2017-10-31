// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Alarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IAlarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Rules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmCountByRuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.IStorageClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.Config;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.AlarmsByRuleController;
import helpers.UnitTest;
import org.eclipse.jetty.util.Callback;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlarmsByRuleControllerTest {
    private static final Logger.ALogger log = Logger.of(AlarmsByRuleControllerTest.class);
    private AlarmsByRuleController controller;
    private IAlarms alarms;
    private IRules rules;
    private WSClient wsClient;

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
        try {
            IServicesConfig servicesConfig = new Config().getServicesConfig();
            IStorageClient client = mock(IStorageClient.class);
            this.wsClient = mock(WSClient.class);
            this.alarms = new Alarms(servicesConfig, client);
            this.rules = new Rules(servicesConfig, wsClient, alarms);
            this.controller = new AlarmsByRuleController(this.alarms, this.rules);
        } catch (Exception ex) {
            log.error("Exception setting up test", ex);
            Assert.fail(ex.getMessage());
        }
    }

    private Document alarmToDocument(AlarmServiceModel alarm) {

        Document document = new Document();

        // TODO: make inserts idempotent
        document.setId(UUID.randomUUID().toString());
        document.set(docSchemaKey, docSchemaValue);
        document.set(docSchemaVersionKey, docSchemaVersionValue);
        document.set(createdKey, alarm.getDateCreated().getMillis());
        document.set(modifiedKey, alarm.getDateModified().getMillis());
        document.set(statusKey, alarm.getStatus());
        document.set(descriptionKey, alarm.getDescription());
        document.set(deviceIdKey, alarm.getDeviceId());
        document.set(ruleIdKey, alarm.getRuleId());
        document.set(ruleSeverityKey, alarm.getRuleSeverity());
        document.set(ruleDescriptionKey, alarm.getRuleDescription());

        // The logic used to generate the alarm (future proofing for ML)
        document.set("logic", "1Device-1Rule-1Message");

        return document;
    }

    /**
     * Get sample alarm to return to client.
     * TODO: remove after storage dependency is added
     *
     * @return sample alarm
     */
    private AlarmServiceModel getSampleAlarm() {
        return new AlarmServiceModel(
            "6l1log0f7h2yt6p",
            "1234",
            DateTime.parse("2017-02-22T22:22:22-08:00").toInstant().getMillis(),
            DateTime.parse("2017-02-22T22:22:22-08:00").toInstant().getMillis(),
            "Temperature on device x > 75 deg F",
            "group-Id",
            "device-id",
            "open",
            "1234",
            "critical",
            "HVAC temp > 75"
        );
    }

    /**
     * Sample alarms that will be added to the testalarms storage collection
     *
     * @return sample alarm list
     */
    private ArrayList<AlarmServiceModel> getSampleAlarms() {
        ArrayList<AlarmServiceModel> list = new ArrayList<AlarmServiceModel>();
        AlarmServiceModel alarm1 = new AlarmServiceModel(
            null,
            "1",
            DateTime.parse("2017-07-22T22:22:22-08:00").toInstant().getMillis(),
            DateTime.parse("2017-07-22T22:22:22-08:00").toInstant().getMillis(),
            "Temperature on device x > 75 deg F",
            "group-Id",
            "device-id",
            "open",
            "1",
            "critical",
            "HVAC temp > 50"
        );
        AlarmServiceModel alarm2 = new AlarmServiceModel(
            null,
            "2",
            DateTime.parse("2017-06-22T22:22:22-08:00").toInstant().getMillis(),
            DateTime.parse("2017-07-22T22:22:22-08:00").toInstant().getMillis(),
            "Temperature on device x > 75 deg F",
            "group-Id",
            "device-id",
            "acknowledged",
            "2",
            "critical",
            "HVAC temp > 60");
        AlarmServiceModel alarm3 = new AlarmServiceModel(
            null,
            "3",
            DateTime.parse("2017-05-22T22:22:22-08:00").toInstant().getMillis(),
            DateTime.parse("2017-06-22T22:22:22-08:00").toInstant().getMillis(),
            "Temperature on device x > 75 deg F",
            "group-Id",
            "device-id",
            "open",
            "3",
            "info",
            "HVAC temp > 70");
        AlarmServiceModel alarm4 = new AlarmServiceModel(
            null,
            "4",
            DateTime.parse("2017-04-22T22:22:22-08:00").toInstant().getMillis(),
            DateTime.parse("2017-06-22T22:22:22-08:00").toInstant().getMillis(),
            "Temperature on device x > 75 deg F",
            "group-Id",
            "device-id",
            "closed",
            "4",
            "warning",
            "HVAC temp > 80");
        list.add(alarm1);
        list.add(alarm2);
        list.add(alarm3);
        list.add(alarm4);
        return list;
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void provideAlarmsByRuleByIdResult() throws Exception {
        ArrayList<AlarmServiceModel> alarmResult = new ArrayList<AlarmServiceModel>() {{
            add(new AlarmServiceModel());
            add(new AlarmServiceModel());
        }};

        IAlarms alarms = mock(IAlarms.class);
        IRules rules = mock(IRules.class);
        AlarmsByRuleController controller = new AlarmsByRuleController(alarms, rules);
        when(alarms.getListByRuleId(
            "1", DateTime.now(), DateTime.now(), "asc", 0, 100, new String[0]))
            .thenReturn(alarmResult);

        // Act
        Result response = controller.get("", null, null, null, 0, 0, null);

        // Assert
        assertThat(response.body().isKnownEmpty(), is(false));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void provideAlarmsByRuleListResult() throws Exception {

        // Arrange
        ConditionServiceModel sampleCondition = new ConditionServiceModel(
            "TestField",
            "Equals",
            "TestValue"
        );
        ArrayList<ConditionServiceModel> sampleConditions = new ArrayList<>();
        sampleConditions.add(sampleCondition);

        RuleServiceModel sampleRule = new RuleServiceModel(
            "TestName",
            true,
            "Test Description",
            "TestGroup",
            "critical",
            sampleConditions
        );

        // TODO Fix Tests https://github.com/Azure/device-telemetry-java/issues/99
        /*
        // sample rules
        ArrayList<RuleServiceModel> ruleList = new ArrayList<>();

        CompletionStage<List<RuleServiceModel>> ruleListResult =
            Callback.Completable.completedFuture(ruleList);

        WSRequest mockRequest = mock(WSRequest.class);
        CompletionStage<WSResponse> mockResponse =
            Callback.Completable.completedFuture(mock(WSResponse.class));

        when(mockRequest.addHeader(anyString(), anyString()))
            .thenReturn(mockRequest);

        when(this.wsClient.url(anyString()))
            .thenReturn(mockRequest);

        when(mockRequest.get())
            .thenReturn(mockResponse);

        when(this.rules.getListAsync("asc", 0, 100, null))
            .thenReturn(ruleListResult);

        // sample alarms
        ArrayList<AlarmCountByRuleServiceModel> alarmList = new ArrayList<>();
        alarmList.add(new AlarmCountByRuleServiceModel(5, "open", DateTime.now(), sampleRule));

        CompletionStage<List<AlarmCountByRuleServiceModel>> alarmListResult =
            Callback.Completable.completedFuture(alarmList);

        when(this.rules.getAlarmCountForList(
            DateTime.parse("2017-10-18T19:53:49"),
            DateTime.parse("2017-10-18T19:53:49"),
            "asc",
            0,
            100,
            new String[0]))
            .thenReturn(alarmListResult);

        // Act
        this.controller.listAsync(
            "2017-10-18T19:53:49",
            "2017-10-18T19:53:49",
            "asc",
            0,
            100,
            "")
            .thenApply(response -> {
                // Assert
                assertThat(response.body().isKnownEmpty(), is(false));
                return null;
            });
        */
    }
}
