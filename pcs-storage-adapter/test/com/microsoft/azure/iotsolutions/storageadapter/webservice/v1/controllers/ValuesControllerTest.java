package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.storageadapter.services.IKeyValueContainer;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.wrappers.GuidKeyGenerator;
import helpers.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.libs.Json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ValuesControllerTest {

    private GuidKeyGenerator keyGenerator;
    private IKeyValueContainer mockStorage;
    private ValuesController controller;

    @Before
    public void setUp() throws Exception {
        mockStorage = Mockito.mock(IKeyValueContainer.class);
        keyGenerator = new GuidKeyGenerator();
        controller = new ValuesController(mockStorage, keyGenerator);
    }

    @Test(timeout = 100000)
    public void getTest() throws Exception {
        String testKey = keyGenerator.generate();
        String testValue = keyGenerator.generate();
        ValueServiceModel model = new ValueServiceModel(testKey, testValue);
        Mockito.when(mockStorage.get(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(model);
        String resultJson = TestUtils.getString(controller.get(keyGenerator.generate(), keyGenerator.generate()));
        ValueServiceModel result = Json.fromJson(Json.parse(resultJson), ValueServiceModel.class);
        assertEquals(result.Data, testValue);
    }

    @Test(timeout = 100000)
    public void listTest() throws Exception {
        List<ValueServiceModel> models = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ValueServiceModel model = new ValueServiceModel(keyGenerator.generate(), keyGenerator.generate());
            models.add(model);
        }
        Mockito.when(mockStorage.list(Mockito.any(String.class)))
                .thenReturn(models.iterator());
        String resultJson = TestUtils.getString(controller.list(keyGenerator.generate()));
        JsonNode node = Json.parse(resultJson).get("Items");
        Iterator<JsonNode> itr = node.iterator();
        List<ValueServiceModel> result = new ArrayList<>();
        while (itr.hasNext()) {
            ValueServiceModel item = Json.fromJson(itr.next(), ValueServiceModel.class);
            result.add(item);
        }
        assertEquals(result.size(), models.size());
        for (ValueServiceModel item : result) {
            ValueServiceModel model = models.stream()
                    .filter(m -> m.Key.equals(item.Key)).findFirst().get();
            assertEquals(model.Data, item.Data);
        }
    }

    @Test(timeout = 100000)
    public void createTest() throws Exception {
        String testValue = keyGenerator.generate();
        String testKey = keyGenerator.generate();
        ValueServiceModel model = new ValueServiceModel(testKey, testValue);
        Mockito.when(mockStorage.create(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(ValueServiceModel.class)))
                .thenReturn(model);
        TestUtils.setRequest(String.format("{\"Data\":\"%s\"}", keyGenerator.generate(), keyGenerator.generate()));
        String resultJson = TestUtils.getString(controller.put(keyGenerator.generate(), keyGenerator.generate()));
        ValueServiceModel result = Json.fromJson(Json.parse(resultJson), ValueServiceModel.class);
        assertEquals(result.Data, testValue);
    }

    @Test(timeout = 100000)
    public void updateTest() throws Exception {
        String testKey = keyGenerator.generate();
        String testValue = keyGenerator.generate();
        ValueServiceModel model = new ValueServiceModel(testKey, testValue);
        Mockito.when(mockStorage.upsert(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(ValueServiceModel.class)))
                .thenReturn(model);
        TestUtils.setRequest(String.format("{\"ETag\":\"%s\",\"Data\":\"%s\"}", keyGenerator.generate(), keyGenerator.generate()));
        String resultJson = TestUtils.getString(controller.put(keyGenerator.generate(), keyGenerator.generate()));
        ValueServiceModel result = Json.fromJson(Json.parse(resultJson), ValueServiceModel.class);
        assertEquals(result.Data, testValue);
    }

    @Test(timeout = 100000)
    public void deleteTest() throws Exception {
        Mockito.doNothing().when(mockStorage).delete(Mockito.any(String.class), Mockito.any(String.class));
        controller.delete(keyGenerator.generate(), keyGenerator.generate());
    }
}
