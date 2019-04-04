package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.AzureResourceManagerClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IUserManagementClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.UserManagementClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ActionsConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AzureResourceManagerClientTest {

    private final String mockSubscriptionId = "123456abcd";
    private final String mockResourceGroup = "example-name";
    private final String mockArmEndpointUrl = "https://management.azure.com";
    private final String mockApiVersion = "2016-06-01";
    private final String mockUrl = "http://mockurl";

    private WSClient wsClient;
    private WSRequest wsRequest;
    private WSResponse wsResponse;
    private IUserManagementClient mockUserManagementClient;

    private AzureResourceManagerClient client;

    @Before
    public void setUp() {
        this.wsClient = mock(WSClient.class);
        this.wsRequest = mock(WSRequest.class);
        this.wsResponse = mock(WSResponse.class);
        this.mockUserManagementClient = mock(UserManagementClient.class);
        ActionsConfig actionsConfig = new ActionsConfig(
                mockArmEndpointUrl,
                mockApiVersion,
                mockUrl,
                mockResourceGroup,
                mockSubscriptionId);
        ServicesConfig config = new ServicesConfig(
                "http://telemetryurl",
                "http://storageurl",
                "http://usermanagementurl",
                "http://simurl",
                "template",
                "mapsKey",
                actionsConfig);
        this.client = new AzureResourceManagerClient(config,
                this.wsClient,
                this.mockUserManagementClient);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getOffice365IsEnabled_ReturnsTrueIfEnabled() throws
            NotAuthorizedException,
            ExternalDependencyException,
            ExecutionException,
            InterruptedException {
        // Arrange
        when(mockUserManagementClient.getTokenAsync()).thenReturn(CompletableFuture.completedFuture("foo"));
        when(this.wsClient.url(any())).thenReturn(this.wsRequest);
        when(this.wsRequest.get()).thenReturn(CompletableFuture.completedFuture(this.wsResponse));
        when(this.wsResponse.getStatus()).thenReturn(HttpStatus.SC_OK);

        // Act
        boolean result = this.client.isOffice365EnabledAsync().toCompletableFuture().get();

        // Assert
        assertTrue(result);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getOffice365IsEnabled_ReturnsFalseIfDisabled() throws
            NotAuthorizedException,
            ExternalDependencyException,
            ExecutionException,
            InterruptedException {
        // Arrange
        when(this.mockUserManagementClient.getTokenAsync()).thenReturn(CompletableFuture.completedFuture("foo"));
        when(this.wsClient.url(any())).thenReturn(this.wsRequest);
        when(this.wsRequest.get()).thenReturn(CompletableFuture.completedFuture(this.wsResponse));
        when(this.wsResponse.getStatus()).thenReturn(HttpStatus.SC_NOT_FOUND);

        // Act
        boolean result = this.client.isOffice365EnabledAsync().toCompletableFuture().get();

        // Assert
        Assert.assertFalse(result);
    }

    // If the user is not authorized to access the Auth service with their token, this method should also throw.
    @Test(timeout = 100000, expected = NotAuthorizedException.class)
    @Category({UnitTest.class})
    public void getOffice365IsEnabled_ThrowsIfUserManagementNotAuthorized() throws
            NotAuthorizedException,
            ExternalDependencyException,
            ExecutionException,
            InterruptedException {
        // Arrange
        when(mockUserManagementClient.getTokenAsync()).thenThrow(NotAuthorizedException.class);

        // Act
        this.client.isOffice365EnabledAsync().toCompletableFuture().get();
    }

    // If the Logic App testconnection api returns a 401 Unauthorized, then it means the user has access
    // to make the api call but the application is configured incorrectly and needs the user to sign in
    // with their Outlook email account.
    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getOffice365IsEnabled_ReturnsFalseIfNotAuthorized() throws
            NotAuthorizedException,
            ExternalDependencyException,
            ExecutionException,
            InterruptedException {
        // Arrange
        WSRequest wsRequest = mock(WSRequest.class);
        WSResponse wsResponse = mock(WSResponse.class);

        when(mockUserManagementClient.getTokenAsync()).thenReturn(CompletableFuture.completedFuture("foo"));
        when(wsClient.url(any())).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
        when(wsResponse.getStatus()).thenReturn(HttpStatus.SC_UNAUTHORIZED);

        // Act
        boolean result = this.client.isOffice365EnabledAsync().toCompletableFuture().get();

        // Assert
        Assert.assertFalse(result);
    }

    // If the application receives a Forbidden from the Logic App testconnection api,
    // then this method should throw.
    @Test(timeout = 100000, expected = ExecutionException.class)
    @Category({UnitTest.class})
    public void getOffice365IsEnabled_ReturnsFalseIfForbidden() throws
            NotAuthorizedException,
            ExternalDependencyException,
            ExecutionException,
            InterruptedException {

        // Arrange
        WSRequest wsRequest = mock(WSRequest.class);
        WSResponse wsResponse = mock(WSResponse.class);

        when(mockUserManagementClient.getTokenAsync()).thenReturn(CompletableFuture.completedFuture("foo"));
        when(wsClient.url(any())).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
        when(wsResponse.getStatus()).thenReturn(HttpStatus.SC_FORBIDDEN);

        // Act
        boolean result = this.client.isOffice365EnabledAsync().toCompletableFuture().get();
    }
}
