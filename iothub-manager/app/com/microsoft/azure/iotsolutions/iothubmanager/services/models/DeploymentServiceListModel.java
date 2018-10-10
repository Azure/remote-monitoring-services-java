// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import java.util.List;

public class DeploymentServiceListModel {
    private final List<DeploymentServiceModel> items;

    public DeploymentServiceListModel(List<DeploymentServiceModel> items) {
        this.items = items;
    }

    public List<DeploymentServiceModel> getItems() {
        return this.items;
    }
}
