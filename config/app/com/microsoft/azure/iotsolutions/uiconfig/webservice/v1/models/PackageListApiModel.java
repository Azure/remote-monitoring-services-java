// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.Version;
import org.apache.commons.lang3.StringUtils;

import java.util.Hashtable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PackageListApiModel {

    private Iterable<PackageApiModel> items;
    private Hashtable<String, String> metadata;

    @JsonProperty("items")
    public Iterable<PackageApiModel> getItems() {
        return this.items;
    }

    public void setItems(Iterable<PackageApiModel> items) {
        this.items = items;
    }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }

    public PackageListApiModel() {
    }

    public PackageListApiModel(Iterable<Package> models) {
        this(models, null, null);
    }

    /**
     * Filters out packages received from the source by packageType and ConfigType.
     * @param models Package models received from the source
     * @param packageType Type of packages to retain
     * @param configType Config Type of packages to retain
     */
    public PackageListApiModel(Iterable<Package> models, String packageType, String configType) {

        if (StringUtils.isBlank(packageType))
        {
            this.items = StreamSupport.stream(models.spliterator(), false)
                    .map(m -> new PackageApiModel(m)).collect(Collectors.toList());// Backward compatibility
        }
        else if (StringUtils.isBlank(configType))
        {
            this.items = StreamSupport.stream(models.spliterator(), false).map(m -> new PackageApiModel(m))
                    .filter(p -> (
                            StringUtils.isBlank(p.getType().toString())
                            && p.getType().toString().toLowerCase().equals(packageType.toLowerCase().trim())))
                    .collect(Collectors.toList());
        }
        else
        {
            this.items = StreamSupport.stream(models.spliterator(), false).map(m -> new PackageApiModel(m))
                    .filter(p -> (
                            StringUtils.isBlank(p.getType().toString())
                                    && !StringUtils.isBlank(p.getConfigType())
                                    && p.getType().toString().toLowerCase().equals(packageType.toLowerCase().trim())
                                    && p.getConfigType().toLowerCase().equals(configType.toLowerCase().trim())))
                    .collect(Collectors.toList());
        }

        this.metadata = new Hashtable<String, String>();
        this.metadata.put("$type", String.format("Package;%s", Version.Number));
        this.metadata.put("$url", String.format("/%s/packages", Version.Path));
    }
}