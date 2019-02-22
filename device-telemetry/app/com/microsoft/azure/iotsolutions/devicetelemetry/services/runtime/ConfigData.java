package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigData implements IConfigData {

    // Constants
    private static final String CLIENT_ID = "keyVault.appId";
    private static final String CLIENT_SECRET = "keyVault.aadApplicationSecret";
    private static final String KEY_VAULT_NAME = "keyVault.name";
    private static final String READ_FROM_KV_ONLY = "READ-FROM-KV-ONLY";

    // Local environment variables data
    private final Config data;

    // Key Vault
    private KeyVault keyVault;
    private boolean readFromKeyVaultOnly; // Flag to indicate if to read secrets from KV only

    public ConfigData() {
        this.data = ConfigFactory.load();
        // Set up Key Vault
        this.setUpKeyVault();
    }

    @Override
    public String getString(String key) {
        String value = StringUtils.EMPTY;

        if (!this.readFromKeyVaultOnly) {
            value = this.keyVault.getKeyVaultSecret(key);
        } else {
            value = this.data.getString(key);
        }

        return value;
    }

    @Override
    public boolean getBool(String key) {
        boolean defaultValue = false;

        if (!this.readFromKeyVaultOnly) {
            try {
                return this.data.getBoolean(key);
            } catch (ConfigException.Missing e) {
                // Do Nothing as this goes to KV logic (below)
            } catch (ConfigException.WrongType e) {
                return this.stringToBoolean(
                        this.data.getString(key),
                        defaultValue
                );
            }
        }

        return this.stringToBoolean(
                this.keyVault.getKeyVaultSecret(key),
                defaultValue
        );
    }

    @Override
    public int getInt(String key) throws InvalidConfigurationException {
        int defaultValue = 0;

        if (!this.readFromKeyVaultOnly) {
            try {
                return this.data.getInt(key);
            } catch (ConfigException.Missing e) {
                // DO Nothing as this goes to KV logic (below)
            } catch (ConfigException.WrongType e) {
                return this.stringToInt(
                        this.data.getString(key),
                        defaultValue
                );
            }
        }

        return this.stringToInt(
                this.keyVault.getKeyVaultSecret(key),
                defaultValue
        );
    }

    public Duration getDuration(String key) {
        try {
            return this.data.getDuration(key);
        } catch (ConfigException.Missing e) {
            // DO Nothing as this goes to KV logic (below)
        } catch (ConfigException.WrongType e) {
            return Duration.of(
                    Long.valueOf(this.data.getString(key)),
                    ChronoUnit.SECONDS
            );
        }

        return Duration.of(
                Long.valueOf(this.keyVault.getKeyVaultSecret(key)),
                ChronoUnit.SECONDS
        );
    }

    @Override
    public boolean hasPath(String path) {
        if (!this.readFromKeyVaultOnly) {
            return this.data.hasPath(path);
        }
        return this.keyVault.hasPath(path);
    }

    private void setUpKeyVault() {
        String clientId = this.data.getString(CLIENT_ID);
        String clientSecret = this.data.getString(CLIENT_SECRET);
        String keyVaultName = this.data.getString(KEY_VAULT_NAME);

        // Initialize key vault
        this.keyVault = new KeyVault(keyVaultName, clientId, clientSecret);

        // Initialize key vault read only flag
        this.readFromKeyVaultOnly = this.getBool(READ_FROM_KV_ONLY);
    }

    private boolean stringToBoolean(String value, boolean defaultValue) {
        Set knownTrue = new HashSet<String>(Arrays.asList("true", "t", "yes", "y", "1", "-1"));
        Set knownFalse = new HashSet<String>(Arrays.asList("false", "f", "no", "n", "0"));

        if (knownTrue.contains(value)) return true;
        if (knownFalse.contains(value)) return false;

        return defaultValue;
    }

    private int stringToInt(String value, int defaultValue) throws InvalidConfigurationException {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Unable to load configuration value for '{key}'", e);
        } catch (Exception e) {
            // If string value is not found or any other exception than NumberFormat exception.
            return defaultValue;
        }
    }
}
