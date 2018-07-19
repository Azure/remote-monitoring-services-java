// Copyright (c) Microsoft. All rights reserved.

package services.test;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Alarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IAlarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.AlarmsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.StorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.IStorageClient;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

public class AlarmsTest {
    private IStorageClient storageClientMock;
    private IAlarms alarms;

    @Before
    public void setUp() {
        // temporary fix
        String eventHubName = "notificationsystem";
        String logicAppEndPointUrl = "https://prod-00.southeastasia.logic.azure.com:443/workflows/1f2493004aea43e1ac661f071a15f330/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=DIfPL17M7qydXwHxD7g-_K-P3mE6dqYuv7aDfbQji94";
        String eventHubConnectionString = "sb://eventhubnamespace-f3pvd.servicebus.windows.net/;SharedAccessKeyName=NotificationSystem;SharedAccessKey=W8C1Y/ZoBglooXxc1O1r2y5QBl7sa0nIwrYRl5h5YhA=;EntityPath=notificationsystem";
        int eventHubOffsetTimeInMinutes = 0;
        String solutionName = "aayushdemo";
        // setup before every test
        ServicesConfig servicesConfig = new ServicesConfig(
            "storageConnection",
            "storageUrl",
            new StorageConfig(
                "documentdb",
                "connString",
                "database",
                "collection"),
            new AlarmsConfig(
                "documentdb",
                "connString",
                "database",
                "collection",
                3
            ), eventHubName, eventHubConnectionString, eventHubOffsetTimeInMinutes, logicAppEndPointUrl, solutionName);
        this.storageClientMock = Mockito.mock(IStorageClient.class);
        this.alarms = new Alarms(servicesConfig, this.storageClientMock);
    }

    /**
     * Verify basic delete alarms by id behavior.
     * @throws Throwable
     */
    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void DeleteAllAlarmsInList() throws Throwable {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("id1");
        ids.add("id2");
        ids.add("id3");
        ids.add("id4");

        Document d1 = new Document();
        d1.setId("id1");

        Mockito.when(storageClientMock.deleteDocument(anyString(), anyString(), anyString())).thenReturn(d1);

        this.alarms.delete(ids);

        for (int i = 0; i < ids.size(); i++) {
            Mockito.verify(storageClientMock).deleteDocument("database", "collection", ids.get(i));
        }
    }

    /**
     * Verify if delete alarms by id
     * throws generic exception it will fail after 3 tries
     * @throws com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException
     */
    @Test(timeout = 5000, expected = Exception.class)
    @Category({UnitTest.class})
    public void DeleteFailsOnGenericException() throws Throwable {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("failedId");

        Mockito.when(storageClientMock.deleteDocument(anyString(), anyString(), "failedId")).thenThrow(new Exception());

        this.alarms.delete(ids);

        Mockito.verify(storageClientMock, times(3))
                .deleteDocument("database", "collection", "failedId");
    }


    /**
     * Verify if delete alarms by id
     * throws generic exception once it will retry
     */
    @Test(timeout = 5000, expected = Exception.class)
    @Category({UnitTest.class})
    public void DeleteSucceedsAfterTransientException() throws Throwable {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("transientId");

        Document d1 = new Document();
        d1.setId("transientId");

        Mockito.when(storageClientMock.deleteDocument(anyString(), anyString(), "transientId"))
                .thenThrow(new Exception())
                .thenReturn(d1);

        this.alarms.delete(ids);

        Mockito.verify(storageClientMock, times(2))
                .deleteDocument("database", "collection", "id1");
    }
}
