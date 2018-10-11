// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.exceptions.BadRequestException;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.PackageApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.PackageListApiModel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public class PackageController extends Controller {

    private final IStorage storage;
    private static final String PACKAGE_TYPE_PARAM = "Type";
    private static final String FILE_PARAM = "Package";

    @Inject
    public PackageController(IStorage storage) {
        this.storage = storage;
    }

    /**
     * Retrieve all previously uploaded packages
     * @return {@link PackageListApiModel}
     */
    public CompletionStage<Result> getAllAsync() throws BaseException {
        return storage.getAllPackagesAsync().thenApplyAsync(m -> ok(toJson(new PackageListApiModel(m))));
    }

    /**
     * Get a previously created from storage.
     * @param id The id of the package to retrieve from storage.
     * @return {@link PackageApiModel}
     */
    public CompletionStage<Result> getAsync(String id) throws BaseException {
        return storage.getPackageAsync(id).thenApplyAsync(m -> ok(toJson(new PackageApiModel(m))));
    }

    /**
     * Create a package form a multipart form post which expects
     * a "type" and a file uploaded with the name "package".
     * @return Returns the result in the form of {@link PackageApiModel}
     */
    @Authorize("CreatePackages")
    public CompletionStage<Result> createAsync() throws BaseException, BadRequestException, IOException {
        final MultipartFormData formData = request().body().asMultipartFormData();
        if (formData == null) {
            throw new BadRequestException("Multipart form-data is empty");
        }

        final Map<String, String[]> data = formData.asFormUrlEncoded();
        if(!data.containsKey(PACKAGE_TYPE_PARAM) ||
                ArrayUtils.isEmpty(data.get(PACKAGE_TYPE_PARAM)) ||
                StringUtils.isEmpty(data.get(PACKAGE_TYPE_PARAM)[0])) {
            throw new BadRequestException(String.format("Package type not provided. Please specify %s " +
                    "parameter", PACKAGE_TYPE_PARAM));
        }

        final MultipartFormData.FilePart<File> file = formData.getFile(FILE_PARAM);
        if (file == null) {
            throw new BadRequestException(String.format("Package not provided. Please upload a file with " +
                    "attribute name %s", FILE_PARAM));
        }

        final String content = new String(Files.readAllBytes(file.getFile().toPath()));
        final String packageType = data.get(PACKAGE_TYPE_PARAM)[0];
        final PackageApiModel input = new PackageApiModel(file.getFilename(),
                EnumUtils.getEnumIgnoreCase(PackageType.class, packageType), content);
        return storage.addPackageAsync(input.ToServiceModel()).thenApplyAsync(m -> ok(toJson(new
                PackageApiModel(m))));
    }

    /**
     * Deletes a package from storage
     * @param id Id of the package to be deleted
     */
    @Authorize("DeletePackages")
    public CompletionStage<Result> deleteAsync(String id) throws BaseException {
        return storage.deletePackageAsync(id).thenApplyAsync(m -> ok());
    }
}
