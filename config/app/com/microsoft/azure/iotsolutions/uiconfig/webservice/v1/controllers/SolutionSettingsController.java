// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IActions;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.ActionSettingsListApiModel;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public final class SolutionSettingsController extends Controller {

    private static final Logger.ALogger log = Logger.of(SolutionSettingsController.class);
    private final IStorage storage;
    private final IActions actions;
    // 128 KB
    private static final int BUFFER_SIZE = 131072;
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    @Inject
    public SolutionSettingsController(IStorage storage, IActions actions) {
        this.storage = storage;
        this.actions = actions;
    }

    public CompletionStage<Result> getThemeAsync() throws BaseException {
        return storage.getThemeAsync()
                .thenApply(theme -> ok(toJson(theme)));
    }

    public CompletionStage<Result> setThemeAsync() throws BaseException {
        Object theme = new Object();
        JsonNode node = request().body().asJson();
        if (node != null) {
            theme = Json.fromJson(node, Object.class);
        }
        return storage.setThemeAsync(theme)
                .thenApply(result -> ok(toJson(result)));
    }

    public CompletionStage<Result> getLogoAsync() throws BaseException {
        Http.Response response = response();
        return storage.getLogoAsync()
                .thenApply(result -> {
                    return setImageResponse(result, response);
                });
    }

    public CompletionStage<Result> setLogoAsync() throws BaseException {
        Http.RequestBody body = request().body();
        ByteString byteString = body.asBytes();
        byte[] bytes;
        // If byteString is null, need to read bytes from file
        if (byteString == null) {
            bytes = this.convertToByteArray(body.asRaw());
        } else {
            bytes = byteString.toByteBuffer().array();
        }
        Logo model = new Logo();
        if (bytes.length > 0) {
            model.setType(request().contentType().orElse("application/octet-stream"));
            model.setImage(Base64.getEncoder().encodeToString(bytes));
        }
        Optional<String> logoName = request().header(Logo.NAME_HEADER);
        if (logoName.isPresent()) {
            model.setName(logoName.get());
        }
        //for some unknown issue on travis test, make a variable to refer the response in current thread context.
        Http.Response response = response();
        return storage.setLogoAsync(model)
                .thenApply(result -> {
                    return setImageResponse(result, response);
                });
    }

    public Result getActionsSettings() throws BaseException {
        return ok(toJson(new ActionSettingsListApiModel(this.actions.getList())));
    }

    // Given logo and response, sets the body of the response to be the logo image,
    // if not null.
    // Also sets the following headers in response:
    // Name: Logo Name (if not null)
    // IsDefault: Boolean, if Logo is default
    // Access_Control_Expose_Headers: Name, IsDefault. This exposes these headers
    private Result setImageResponse(Logo model, Http.Response response) {
        if (model.getName() != null) {
            response.setHeader(Logo.NAME_HEADER, model.getName());
        }
        response.setHeader(Logo.IS_DEFAULT_HEADER, Boolean.toString(model.getDefault()));
        response.setHeader(SolutionSettingsController.ACCESS_CONTROL_EXPOSE_HEADERS,
                Logo.NAME_HEADER + "," + Logo.IS_DEFAULT_HEADER);
        if (model.getImage() == null) {
            return ok();
        }
        return ok(Base64.getDecoder().decode(model.getImage().getBytes())).as(model.getType());
    }

    private byte[] convertToByteArray(Http.RawBuffer rawBuffer) {
        File f = rawBuffer.asFile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        long fileLength = f.length();
        byte[] buf = new byte[fileLength < BUFFER_SIZE ? (int) fileLength : BUFFER_SIZE];
        try {
            FileInputStream fileInputStream = new FileInputStream(f);

            try {
                int len = fileInputStream.read(buf);
                while (len > 0) {
                    outputStream.write(buf, 0, len);
                    len = fileInputStream.read(buf);
                }
                fileInputStream.close();
            } catch (IOException ex) {
                log.warn("Error converting to byte array: ", ex.toString());
                fileInputStream.close();
            }
        } catch (IOException ex) {
            log.warn("Error converting to byte array: ", ex.toString());
        }
        f.delete();
        return outputStream.toByteArray();
    }
}
