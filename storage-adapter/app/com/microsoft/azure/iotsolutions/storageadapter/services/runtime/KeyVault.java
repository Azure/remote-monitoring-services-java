package com.microsoft.azure.iotsolutions.storageadapter.services.runtime;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import com.microsoft.azure.keyvault.models.SecretItem;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KeyVault {

    // Key Vault details and access
    private final String name;
    private final String clientId;
    private final String clientSecret;

    // Key Vault Client
    private final KeyVaultClient keyVaultClient;
    private List<SecretItem> keys;

    // Constants
    private final static String KEY_VAULT_URI = "https://%s.vault.azure.net/secrets/%s";

    public KeyVault(String name, String clientId, String clientSecret) {
        this.name = name;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.keyVaultClient = new KeyVaultClient(createCredentials());
        this.getAllKeys();
    }

    public String getKeyVaultSecret(String secretKey) {
        String uri = String.format(KEY_VAULT_URI, this.name, secretKey);

        try {
            return this.keyVaultClient
                    .getSecret(uri)
                    .value();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasPath(String key) {
        return this.keys.stream()
                .anyMatch(
                        item -> item
                                .id()
                                .toLowerCase()
                                .contains(key.toLowerCase())
                );
    }

    /**
     * Creates a new KeyVaultCredential based on the access token obtained.
     *
     * @return
     */
    private ServiceClientCredentials createCredentials() {
        return new KeyVaultCredentials() {

            //Callback that supplies the token type and access token on request.
            @Override
            public String doAuthenticate(String authorization, String resource, String scope) {

                AuthenticationResult authResult;
                try {
                    authResult = getAccessToken(authorization, resource);
                    return authResult.getAccessToken();
                } catch (Exception e) {
                    // TODO: Add logging
                    // e.printStackTrace();
                }
                return "";
            }

        };
    }

    /**
     * Private helper method that gets the access token for the authorization and resource depending on which variables are supplied in the environment.
     *
     * @param authorization
     * @param resource
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws MalformedURLException
     * @throws Exception
     */
    private AuthenticationResult getAccessToken(String authorization, String resource) throws InterruptedException, ExecutionException, MalformedURLException {

        AuthenticationResult result = null;

        //Starts a service to fetch access token.
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            AuthenticationContext context = new AuthenticationContext(authorization, false, service);

            Future<AuthenticationResult> future = null;

            //Acquires token based on client ID and client secret.
            if (this.clientSecret != null && this.clientSecret != null) {
                ClientCredential credentials = new ClientCredential(this.clientId, this.clientSecret);
                future = context.acquireToken(resource, credentials, null);
            }

            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new RuntimeException("Authentication results were null.");
        }
        return result;
    }

    private void getAllKeys() {
        String uri = String.format(KEY_VAULT_URI, this.name, StringUtils.EMPTY);
        this.keys = this.keyVaultClient.listSecrets(uri);
    }
}
