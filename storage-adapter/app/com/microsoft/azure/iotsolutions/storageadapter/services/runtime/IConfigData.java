package com.microsoft.azure.iotsolutions.storageadapter.services.runtime;

import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidConfigurationException;

public interface IConfigData {
    String getString(String key);
    boolean getBool(String key);
    int getInt(String key) throws InvalidConfigurationException;
    boolean hasPath(String path);
}
