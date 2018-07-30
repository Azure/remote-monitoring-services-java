// Copyright (c) Microsoft. All rights reserved.

package services.test.helpers;

import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers.QueryBuilder;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class QueryBuilderTest {

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void getDocumentsSql_WithValidInput() throws Throwable {
        // Arrange
        DateTime from = DateTime.now().minusHours(-1);
        DateTime to = DateTime.now();

        // Act
        SqlQuerySpec querySpec = QueryBuilder.getDocumentsSQL(
            "alarm",
            "bef978d4-54f6-429f-bda5-db2494b833ef",
            "rule.id",
            from,
            "device.msg.received",
            to,
            "device.msg.received",
            "asc",
            "device.msg.received",
            0,
            100,
            new String[]{"chiller-01.0", "chiller-02.0"},
            "device.id");

        // Assert
        String expectedQueryText = String.format("SELECT TOP @top * FROM c WHERE (c['doc.schema'] = @schemaName AND c[@devicesProperty] IN (@devicesParameterName0,@devicesParameterName1) AND c[@byIdProperty] = @byId AND c[@fromProperty] >= @from AND c[@toProperty] <= @to) ORDER BY c[@orderProperty] ASC",
            from.getMillis(), to.getMillis());
        Assert.assertEquals(expectedQueryText, querySpec.getQueryText());
        SqlParameter[] parameters = querySpec.getParameters().toArray(new SqlParameter[0]);
        Assert.assertEquals(100, parameters[0].get("value"));
        Assert.assertEquals("alarm", parameters[1].get("value"));
        Assert.assertEquals("device.id", parameters[2].get("value"));
        Assert.assertEquals("chiller-01.0", parameters[3].get("value"));
        Assert.assertEquals("chiller-02.0", parameters[4].get("value"));
        Assert.assertEquals("rule.id", parameters[5].get("value"));
        Assert.assertEquals("bef978d4-54f6-429f-bda5-db2494b833ef", parameters[6].get("value"));
        Assert.assertEquals("device.msg.received", parameters[7].get("value"));
        Assert.assertEquals(from.getMillis(), parameters[8].get("value"));
        Assert.assertEquals("device.msg.received", parameters[9].get("value"));
        Assert.assertEquals(to.getMillis(), parameters[10].get("value"));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void getDocumentsSql_WithNullIdProperty() throws Throwable {
        // Arrange
        DateTime from = DateTime.now().minusHours(-1);
        DateTime to = DateTime.now();

        // Act
        SqlQuerySpec querySpec = QueryBuilder.getDocumentsSQL(
            "alarm",
            null,
            null,
            from,
            "device.msg.received",
            to,
            "device.msg.received",
            "asc",
            "device.msg.received",
            0,
            100,
            new String[]{"chiller-01.0", "chiller-02.0"},
            "device.id");

        // Assert
        String expectedQueryText = String.format("SELECT TOP @top * FROM c WHERE (c['doc.schema'] = @schemaName AND c[@devicesProperty] IN (@devicesParameterName0,@devicesParameterName1) AND c[@fromProperty] >= @from AND c[@toProperty] <= @to) ORDER BY c[@orderProperty] ASC",
            from.getMillis(), to.getMillis());
        Assert.assertEquals(expectedQueryText, querySpec.getQueryText());
        SqlParameter[] parameters = querySpec.getParameters().toArray(new SqlParameter[0]);
        Assert.assertEquals(100, parameters[0].get("value"));
        Assert.assertEquals("alarm", parameters[1].get("value"));
        Assert.assertEquals("device.id", parameters[2].get("value"));
        Assert.assertEquals("chiller-01.0", parameters[3].get("value"));
        Assert.assertEquals("chiller-02.0", parameters[4].get("value"));
        Assert.assertEquals("device.msg.received", parameters[5].get("value"));
        Assert.assertEquals(from.getMillis(), parameters[6].get("value"));
        Assert.assertEquals("device.msg.received", parameters[7].get("value"));
        Assert.assertEquals(to.getMillis(), parameters[8].get("value"));
    }

    @Test(timeout = 5000, expected = InvalidInputException.class)
    @Category({UnitTest.class})
    public void failToGetDocumentsSql_WithInvalidInput() throws Throwable {
        // Arrange
        DateTime from = DateTime.now().minusHours(-1);
        DateTime to = DateTime.now();

        // Assert
        QueryBuilder.getDocumentsSQL(
            "alarms' or '1'='1",
            "bef978d4-54f6-429f-bda5-db2494b833ef",
            "rule.id",
            from,
            "device.msg.received",
            to,
            "device.msg.received",
            "asc",
            "device.msg.received",
            0,
            100,
            new String[]{"chiller-01.0", "chiller-02.0"},
            "deviceId");
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void getCountSql_WithValidInput() throws Throwable {
        // Arrange
        DateTime from = DateTime.now().minusHours(-1);
        DateTime to = DateTime.now();

        // Act
        SqlQuerySpec querySpec = QueryBuilder.getCountSQL(
            "alarm",
            "bef978d4-54f6-429f-bda5-db2494b833ef",
            "rule.id",
            from,
            "device.msg.received",
            to,
            "device.msg.received",
            new String[]{"chiller-01.0", "chiller-02.0"},
            "device.id",
            new String[]{"open", "acknowledged"},
            "status");

        // Assert
        String expectedQueryText = String.format("SELECT VALUE COUNT(1) FROM c WHERE (c['doc.schema'] = @schemaName AND c[@devicesProperty] IN (@devicesParameterName0,@devicesParameterName1) AND c[@byIdProperty] = @byId AND c[@fromProperty] >= @from AND c[@toProperty] <= @to AND c[@filterProperty] IN (@filterParameterName0,@filterParameterName1))",
            from.getMillis(), to.getMillis());
        Assert.assertEquals(expectedQueryText, querySpec.getQueryText());
        SqlParameter[] parameters = querySpec.getParameters().toArray(new SqlParameter[0]);
        Assert.assertEquals("alarm", parameters[0].get("value"));
        Assert.assertEquals("device.id", parameters[1].get("value"));
        Assert.assertEquals("chiller-01.0", parameters[2].get("value"));
        Assert.assertEquals("chiller-02.0", parameters[3].get("value"));
        Assert.assertEquals("rule.id", parameters[4].get("value"));
        Assert.assertEquals("bef978d4-54f6-429f-bda5-db2494b833ef", parameters[5].get("value"));
        Assert.assertEquals("device.msg.received", parameters[6].get("value"));
        Assert.assertEquals(from.getMillis(), parameters[7].get("value"));
        Assert.assertEquals("device.msg.received", parameters[8].get("value"));
        Assert.assertEquals(to.getMillis(), parameters[9].get("value"));
        Assert.assertEquals("status", parameters[10].get("value"));
        Assert.assertEquals("open", parameters[11].get("value"));
        Assert.assertEquals("acknowledged", parameters[12].get("value"));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void getCountSql_WithNullIdProperty() throws Throwable {
        // Arrange
        DateTime from = DateTime.now().minusHours(-1);
        DateTime to = DateTime.now();

        // Act
        SqlQuerySpec querySpec = QueryBuilder.getCountSQL(
            "alarm",
            null,
            null,
            from,
            "device.msg.received",
            to,
            "device.msg.received",
            new String[]{"chiller-01.0", "chiller-02.0"},
            "device.id",
            new String[]{"open", "acknowledged"},
            "status");

        // Assert
        String expectedQueryText = String.format("SELECT VALUE COUNT(1) FROM c WHERE (c['doc.schema'] = @schemaName AND c[@devicesProperty] IN (@devicesParameterName0,@devicesParameterName1) AND c[@fromProperty] >= @from AND c[@toProperty] <= @to AND c[@filterProperty] IN (@filterParameterName0,@filterParameterName1))",
            from.getMillis(), to.getMillis());
        Assert.assertEquals(expectedQueryText, querySpec.getQueryText());
        SqlParameter[] parameters = querySpec.getParameters().toArray(new SqlParameter[0]);
        Assert.assertEquals("alarm", parameters[0].get("value"));
        Assert.assertEquals("device.id", parameters[1].get("value"));
        Assert.assertEquals("chiller-01.0", parameters[2].get("value"));
        Assert.assertEquals("chiller-02.0", parameters[3].get("value"));
        Assert.assertEquals("device.msg.received", parameters[4].get("value"));
        Assert.assertEquals(from.getMillis(), parameters[5].get("value"));
        Assert.assertEquals("device.msg.received", parameters[6].get("value"));
        Assert.assertEquals(to.getMillis(), parameters[7].get("value"));
        Assert.assertEquals("status", parameters[8].get("value"));
        Assert.assertEquals("open", parameters[9].get("value"));
        Assert.assertEquals("acknowledged", parameters[10].get("value"));
    }

    @Test(timeout = 5000, expected = InvalidInputException.class)
    @Category({UnitTest.class})
    public void failToGetCountSql_WithInvalidInput() throws Throwable {
        // Arrange
        DateTime from = DateTime.now().minusHours(-1);
        DateTime to = DateTime.now();

        // Assert
        QueryBuilder.getCountSQL(
            "alarm",
            "rule1' or '1'='1",
            "rule.id",
            from,
            "device.msg.received",
            to,
            "device.msg.received",
            new String[]{"chiller-01.0", "chiller-02.0"},
            "device.id",
            new String[]{"open", "acknowledged"},
            "status");
    }
}
