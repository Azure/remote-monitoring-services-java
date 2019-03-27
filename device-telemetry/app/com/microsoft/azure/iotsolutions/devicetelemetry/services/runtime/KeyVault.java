package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import com.microsoft.azure.keyvault.models.SecretItem;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

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

    private static final Logger.ALogger log = Logger.of(KeyVault.class);

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
        secretKey = this.processSecretKey(secretKey);
        String uri = String.format(KEY_VAULT_URI, this.name, secretKey);

        try {
            return this.keyVaultClient
                    .getSecret(uri)
                    .value();
        } catch (Exception e) {
            String message = String.format("Failed to get the secret {%s} from the key vault.", secretKey);
            log.error(message, e);
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

    // Get last token of the key for referencing the key vault:
    // (local setting file ) => key vault denomination
    // messages.cosmosdb.documentDbConnectionString => documentDbConnectionString
    private String processSecretKey(String secretKey) {
        return secretKey.substring(secretKey.lastIndexOf(".") + 1);
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
                    log.error("Failed to get authentication token for key vault.",e);
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
            if (!StringUtils.isEmpty(this.clientSecret) && !StringUtils.isEmpty(this.clientId)) {
                ClientCredential credentials = new ClientCredential(this.clientId, this.clientSecret);
                future = context.acquireToken(resource, credentials, null);
            }

            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            log.error("Failed to get authentication token for key vault.");
            throw new RuntimeException("Authentication results were null.");
        }
        return result;
    }

    private void getAllKeys() {
        String uri = String.format(KEY_VAULT_URI, this.name, StringUtils.EMPTY);
        this.keys = this.keyVaultClient.listSecrets(uri);
    }
}
