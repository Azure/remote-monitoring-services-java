// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.controllers;

import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.iotsolutions.storageadapter.services.DocDBKeyValueContainer;
import com.microsoft.azure.iotsolutions.storageadapter.services.IKeyValueContainer;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.CreateResourceException;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.storageadapter.services.wrappers.DocumentClientFactory;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime.Config;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Iterator;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class KeyValueControllerTest {

    private ValueServiceModel inModel;
    private IKeyValueContainer container;
    private String collectionId = "Test-CollectionId";
    private String key = "Test-Key";
    private String value = "Test-Value";

    @Before
    public void setUp() throws DocumentClientException, InvalidConfigurationException, CreateResourceException {
        // something before every test
        Config config = new Config();
        IServicesConfig servicesConfig = config.getServicesConfig();
        DocumentClientFactory factory = new DocumentClientFactory(config);
        container = new DocDBKeyValueContainer(factory, servicesConfig);
        inModel = new ValueServiceModel(value);
        container.upsert(collectionId, key, inModel);
    }

    @After
    public void tearDown() throws DocumentClientException {
        // something after every test
        container.delete(collectionId, key);
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void getTest() throws DocumentClientException {
        ValueServiceModel value = container.get(collectionId, key);
        assertThat(value.Key, is(key));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void listTest() throws DocumentClientException {
        Iterator<ValueServiceModel> value = container.list(collectionId);
        assertNotNull(value);
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void putTest() throws DocumentClientException {
        String inputKey = key + UUID.randomUUID().toString();
        ValueServiceModel value = container.create(collectionId, inputKey, inModel);
        assertThat(value.Key, is(inputKey));
        container.delete(collectionId, inputKey);
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void postTest() throws DocumentClientException {
        String inputKey = key + UUID.randomUUID().toString();
        ValueServiceModel value = container.upsert(collectionId, inputKey, inModel);
        assertThat(value.Key, is(inputKey));
        container.delete(collectionId, inputKey);
    }

}
