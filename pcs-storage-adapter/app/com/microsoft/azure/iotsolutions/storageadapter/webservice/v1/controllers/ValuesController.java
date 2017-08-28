// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.iotsolutions.storageadapter.services.IKeyValueContainer;
import com.microsoft.azure.iotsolutions.storageadapter.services.helpers.DocumentIdHelper;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.exceptions.BadRequestException;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models.ValueApiModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models.ValueListApiModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.wrappers.IKeyGenerator;
import play.Logger;
import play.libs.Json;
import play.mvc.Http.Status;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Results.*;

/**
 * Service health check endpoint.
 */
@Singleton
public final class ValuesController {

    private static final Logger.ALogger log = Logger.of(ValuesController.class);
    private IKeyValueContainer container;
    private IKeyGenerator keyGenerator;

    @Inject
    public ValuesController(IKeyValueContainer container, IKeyGenerator keyGenerator) {
        this.container = container;
        this.keyGenerator = keyGenerator;
    }

    /**
     * Get value by collectionId and key
     *
     * @return result
     */
    public Result get(String collectionId, String key) throws Exception {
        ensureValidId(collectionId, key);
        try {
            ValueServiceModel result = this.container.get(collectionId, key);
            return ok(toJson(new ValueApiModel(result)));
        } catch (Exception ex) {
            log.error("collectionId=" + collectionId + ", key=" + key + ", " + ex.getMessage());
            return checkDocumentClientException(ex);
        }
    }

    /**
     * Get values list with collectionId
     *
     * @return result
     */
    public Result list(String collectionId) throws Exception {
        try {
            Iterator<ValueServiceModel> result = this.container.list(collectionId);
            return ok(toJson(new ValueListApiModel(result, collectionId)));
        } catch (Exception ex) {
            log.error("collectionId=" + collectionId + ", " + ex.getMessage());
            return checkDocumentClientException(ex);
        }
    }

    /**
     * Create a new item
     *
     * @return result
     */
    public Result post(String collectionId) throws Exception {
        String key = keyGenerator.generate();
        ensureValidId(collectionId, key);
        try {
            JsonNode jsonBody = request().body().asJson();
            ValueServiceModel inputModel = Json.fromJson(jsonBody, ValueServiceModel.class);
            ValueServiceModel result = this.container.create(collectionId, key, inputModel);
            return ok(toJson(new ValueApiModel(result)));
        } catch (Exception ex) {
            log.error("collectionId=" + collectionId + ", " + ex.getMessage());
            return checkDocumentClientException(ex);
        }
    }

    /**
     * Create or update an item
     *
     * @return result
     */
    public Result put(String collectionId, String key) throws Exception {
        ensureValidId(collectionId, key);
        try {
            JsonNode jsonBody = request().body().asJson();
            ValueServiceModel inputModel = Json.fromJson(jsonBody, ValueServiceModel.class);
            ValueServiceModel result = (inputModel.ETag == null) ?
                    this.container.create(collectionId, key, inputModel) :
                    this.container.upsert(collectionId, key, inputModel);
            return ok(toJson(new ValueApiModel(result)));
        } catch (DocumentClientException ex) {
            log.error("collectionId=" + collectionId + ", key=" + key + ", " + ex.getMessage());
            return checkDocumentClientException(ex);
        }
    }

    /**
     * Remove item
     *
     * @return result
     */
    public Result delete(String collectionId, String key) throws Exception {
        ensureValidId(collectionId, key);
        try {
            this.container.delete(collectionId, key);
            return ok();
        } catch (Exception ex) {
            log.error("collectionId=" + collectionId + ", key=" + key + ", " + ex.getMessage());
            return checkDocumentClientException(ex);
        }
    }


    private Result checkDocumentClientException(Exception exception) throws JsonProcessingException {
        DocumentClientException ex = (DocumentClientException) exception;
        if (ex != null) {
            String errorMessage;
            switch (ex.getStatusCode()) {
                case Status.NOT_FOUND:
                    errorMessage = "The resource requested doesn't exist.";
                    break;
                case Status.CONFLICT:
                    errorMessage = "There is already a key with the Id specified.";
                    break;
                case Status.PRECONDITION_FAILED:
                    errorMessage = "ETag mismatch: the resource has been updated by another client.";
                    break;
                default:
                    errorMessage = ex.getMessage();
                    break;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("Message", "An error has occurred.");
            data.put("ExceptionMessage", errorMessage);
            data.put("ExceptionType", ex.getClass().getName());
            String json = new ObjectMapper().writeValueAsString(data);
            return status(ex.getStatusCode(), json);
        }
        return notAcceptable("");
    }

    private void ensureValidId(String collectionId, String key) throws BadRequestException {
        String id = DocumentIdHelper.GenerateId(collectionId, key);
        if (id.length() > 255) {
            String message = "The collectionId/Key are too long: " + collectionId + "/" + key;
            throw new BadRequestException(message);
        }
    }
}
