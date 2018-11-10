// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.IActionSettings;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(Actions.class)
public interface IActions {
    List<IActionSettings> getList() throws ExternalDependencyException;
}
