package com.microsoft.azure.iotsolutions.iothubmanager.services.runtime;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidConfigurationException;
import java.util.List;

public interface IConfigData {
    String getString(String key);
    boolean getBool(String key);
    int getInt(String key) throws InvalidConfigurationException;
    boolean hasPath(String path);
    List<String> getStringList(String key) throws InvalidConfigurationException;
}
