package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import play.Logger;
import play.libs.Json;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class UserManagementClient implements IUserManagementClient {
    private final IHttpClient httpClient;
    private static final Logger.ALogger log = Logger.of(StorageAdapterClient.class);
    private final String serviceUri;

    public UserManagementClient(IHttpClient httpClient, IServicesConfig config) {
        this.httpClient = httpClient;
        this.serviceUri = config.getUserManagementApiUrl();
    }

    @Override
    public CompletionStage<List<String>> getAllowedActionsAsync(String userObjectId, List<String> roles)
            throws ResourceNotFoundException, CompletionException {
        try {

            HttpRequest request = this.createRequest(String.format("users/%s/allowedActions", userObjectId), roles);
            IHttpResponse response = httpClient.getAsync(request).toCompletableFuture().get();
            this.checkStatusCode(response, request);
            ObjectMapper mapper = new ObjectMapper();

            return CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return mapper.readValue(response.getContent(), new TypeReference<List<String>>(){});
                        } catch(Exception e) {
                            throw new CompletionException(e);
                        }
                    });
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CompletionException(String.format("Unable to retrieve user %s from auth service", userObjectId), e);
        }
    }

    private HttpRequest createRequest(String path, List<String> content)  throws InvalidConfigurationException {
        try {
            HttpRequest request = new HttpRequest();
            request.setUriFromString(this.serviceUri + "/" + path);
            request.getOptions().setAllowInsecureSSLServer(true);
            if (content != null) {
                request.setContent(content);
            }
            return request;
        }  catch (UnsupportedEncodingException | URISyntaxException e) {
            throw new InvalidConfigurationException("Unable to create http request", e);
        }
    }


    private HttpRequest createRequest(String path) throws InvalidConfigurationException {
        return createRequest(path, null);
    }

    private void checkStatusCode(IHttpResponse response, IHttpRequest request) throws BaseException {
        if (response.isSuccessStatusCode()) {
            return;
        }
        log.info(String.format("Auth service returned %s for request %s",
                response.getStatusCode(), request.getUri().toString()));
        switch (response.getStatusCode()) {
            case HttpStatus.SC_NOT_FOUND:
                throw new ResourceNotFoundException(
                        response.getContent() + ", request URL = " + request.getUri().toString());

            default:

                throw new ExternalDependencyException(
                        String.format("Http request failed, status code = %s, content = %s, request URL = %s",
                                response.getStatusCode(), response.getContent(), request.getUri().toString()));
        }
    }
}
