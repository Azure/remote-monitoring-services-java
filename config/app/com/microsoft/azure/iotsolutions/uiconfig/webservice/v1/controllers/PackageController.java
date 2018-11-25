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
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.ConfigTypeListApiModel;
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
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

@Singleton
public class PackageController extends Controller {

    private final IStorage storage;
    private static final String PACKAGE_TYPE_PARAM = "PackageType";
    private static final String PACKAGE_CONFIG_TYPE_PARAM = "ConfigType";
    private static final String FILE_PARAM = "Package";

    @Inject
    public PackageController(IStorage storage) {
        this.storage = storage;
    }

    /**
     * This function can be used to get packages with and without parameters
     * PackageType, ConfigType. Without the query params this will return all
     * the packages.
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> getFilteredAsync(String packageType, String configType) throws BaseException,
            BadRequestException, ExecutionException, InterruptedException {

        if (packageType == null && configType == null) {
            return storage.getAllPackagesAsync().thenApplyAsync(m -> ok(toJson(new PackageListApiModel(m))));
        }

        if (packageType == null) {
            throw new BadRequestException("Package Type is empty");
        }
        return storage.getFilteredPackagesAsync(packageType, configType)
                .thenApplyAsync(m -> ok(toJson(new PackageListApiModel(m))));
    }

    /**
     * Get a list of previously created configTypes from storage
     * @param id The id of the package to retrieve from storage.
     * @return {@link PackageApiModel}
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> getAsync(String id) throws BaseException {
        return storage.getPackageAsync(id).thenApplyAsync(m -> ok(toJson(new PackageApiModel(m))));
    }

    /**
     * Create a package from a multipart form post which expects
     * a "type" and a file uploaded with the name "package".
     * @return Returns the result in the form of {@link PackageApiModel}
     */
    @Authorize("CreatePackages")
    public CompletionStage<Result> createAsync() throws
            BaseException,
            BadRequestException,
            IOException,
            ExecutionException,
            InterruptedException {
        final MultipartFormData formData = request().body().asMultipartFormData();
        if (formData == null) {
            throw new BadRequestException("Multipart form-data is empty");
        }

        /**
         * The form is sending multipart form/data content type. This is so that the form data can handle any possible
         * form input types that may come in. In this case it is just a single value for each field, but if we ever
         * included multiple file select for example then it could be more values.
         */
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
        String configType = data.get(PACKAGE_CONFIG_TYPE_PARAM)[0];

        if (packageType.equals(PackageType.edgeManifest.toString()) &&
                !(StringUtils.isBlank(configType)))
        {
            throw new BadRequestException("Package of type EdgeManifest cannot have parameter " +
                    "configType.");
        }

        if (configType == null)
        {
            configType = StringUtils.EMPTY;
        }

        final PackageApiModel input = new PackageApiModel(file.getFilename(),
                EnumUtils.getEnumIgnoreCase(PackageType.class, packageType),
                configType,
                content);

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
