// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Hashtable;

public class ValueListApiModel {

    public Iterable<ValueApiModel> Items;

    @JsonProperty("$metadata")
    public Hashtable<String, String> Metadata;
}
