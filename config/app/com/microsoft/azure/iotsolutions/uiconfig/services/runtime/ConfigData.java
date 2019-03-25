package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigData implements IConfigData {

    // Constants
    public final String APPLICATION_KEY;
    private static final String CLIENT_ID = "keyvault.aadAppId";
    private static final String CLIENT_SECRET = "keyvault.aadAppSecret";
    private static final String KEY_VAULT_NAME = "keyvault.name";

    private static final Logger.ALogger log = Logger.of(ConfigData.class);

    // Local environment variables data
    private final Config data;

    // Key Vault
    private KeyVault keyVault;

    public ConfigData(String applicationKey) {
        this.APPLICATION_KEY = applicationKey;
        this.data = ConfigFactory.load();
        // Set up Key Vault
        this.setUpKeyVault();
    }

    @Override
    public String getString(String key) {
        String value = StringUtils.EMPTY;

        value = this.data.getString(key);

        if (StringUtils.isEmpty(value)) {
            String message = String.format("Value for secret %s not found in local env. " +
                    " Trying to get the secret from KeyVault.", key);
            log.warn(message);

            value = this.keyVault.getKeyVaultSecret(key);
        }

        return value;
    }

    @Override
    public boolean getBool(String key) {
        Boolean value = false;

        try {
            value = this.data.getBoolean(key);
        } catch (ConfigException.Missing e) {
            // Do Nothing as this goes to KV logic (below)
            String message = String.format("Failed to get the secret %s from application.conf.", key);
            log.error(message, e);
        } catch (ConfigException.WrongType e) {
            // Try to get this as a String and
            value = this.stringToBoolean(
                    this.data.getString(key),
                    null
            );
        }

        if (value == null) {
            String message = String.format("Value for secret %s not found in local env. " +
                    " Trying to get the secret from KeyVault.", key);
            log.warn(message);

            value = this.stringToBoolean(
                    this.keyVault.getKeyVaultSecret(key),
                    false
            );
        }

        return value;
    }

    @Override
    public int getInt(String key) throws InvalidConfigurationException {
        Integer value = null;

        try {
            value = this.data.getInt(key);
        } catch (ConfigException.Missing e) {
            // Do Nothing as this goes to KV logic (below)
            String message = String.format("Failed to get the secret %s from application.conf.", key);
            log.error(message, e);
        } catch (ConfigException.WrongType e) {
            // Try to get this as a String and
            value = this.stringToInt(
                    this.data.getString(key),
                    null
            );
        }

        if (value == null) {
            String message = String.format("Value for secret %s not found in local env. " +
                    " Trying to get the secret from KeyVault.", key);
            log.warn(message);

            value = this.stringToInt(
                    this.keyVault.getKeyVaultSecret(key),
                    0
            );
        }

        return value;
    }

    public Duration getDuration(String key) {
        Duration value = null;

        try {
            value = this.data.getDuration(key);
        } catch (ConfigException.Missing e) {
            // Do Nothing as this goes to KV logic (below)
            String message = String.format("Failed to get the secret %s from application.conf.", key);
            log.error(message, e);
        } catch (ConfigException.WrongType e) {
            // Try to get this as a String and
            value = Duration.of(
                        Long.valueOf(this.data.getString(key)),
                        ChronoUnit.SECONDS
            );
        }

        if (value == null) {
            String message = String.format("Value for secret %s not found in local env. " +
                    " Trying to get the secret from KeyVault.", key);
            log.warn(message);

            value = Duration.of(
                    Long.valueOf(this.keyVault.getKeyVaultSecret(key)),
                    ChronoUnit.SECONDS
            );
        }

        return value;
    }

    /**
     * Checks if particular key is present in the secrets (conf file OR KeyVault)
     */
    @Override
    public boolean hasPath(String path) {
        boolean value = this.data.hasPath(path);
        if (!value) {
            return this.keyVault.hasPath(path);
        }
        return value;
    }

    private void setUpKeyVault() {
        String clientId = this.data.getString(APPLICATION_KEY+CLIENT_ID);
        String clientSecret = this.data.getString(APPLICATION_KEY+CLIENT_SECRET);
        String keyVaultName = this.data.getString(APPLICATION_KEY+KEY_VAULT_NAME);

        // Initialize key vault
        this.keyVault = new KeyVault(keyVaultName, clientId, clientSecret);
    }

    private Boolean stringToBoolean(String value, Boolean defaultValue) {
        Set knownTrue = new HashSet<String>(Arrays.asList("true", "t", "yes", "y", "1", "-1"));
        Set knownFalse = new HashSet<String>(Arrays.asList("false", "f", "no", "n", "0"));

        if (knownTrue.contains(value)) return true;
        if (knownFalse.contains(value)) return false;

        return defaultValue;
    }

    private int stringToInt(String value, Integer defaultValue) throws InvalidConfigurationException {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Unable to load configuration value for '{key}'", e);
        } catch (Exception e) {
            // If string value is not found or any other exception than NumberFormat exception.
            String message = String.format("Failed to convert %s value to integer.", value);
            log.warn(message, e);

            return defaultValue;
        }
    }
}
