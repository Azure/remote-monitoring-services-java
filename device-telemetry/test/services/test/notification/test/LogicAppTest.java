// Copyright (c) Microsoft. All rights reserved.

package services.test.notification.test;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

public class LogicAppTest {
    private static final Logger.ALogger log = Logger.of(LogicAppTest.class);
    private CloseableHttpClient client;

    @Before
    public void setUp(){
        this.client = HttpClients.createDefault();
    }

    @Test
    public void ReturnInvalidStatusCodeWhenInvalidEndpointUrl(){

    }

    @Test
    public void ReturnOkStatusCodeWhenValidEndpointUrl(){

    }


}
