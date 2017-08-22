// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;

public class ValueListApiModel {

    public Iterable<ValueApiModel> Items;

    @SerializedName("$metadata")
    public Hashtable<String, String> Metadata;
}
