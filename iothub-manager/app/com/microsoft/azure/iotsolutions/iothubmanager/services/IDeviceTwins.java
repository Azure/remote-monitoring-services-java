// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinServiceModel;

import java.util.ArrayList;

// TODO: documentation

@ImplementedBy(DeviceTwins.class)
public interface IDeviceTwins {
     DeviceTwinServiceModel get(final String id);

     ArrayList<DeviceTwinServiceModel> getList();
}
