// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;

import java.util.concurrent.CompletionStage;

@ImplementedBy(Seed.class)
public interface ISeed {
    CompletionStage trySeedAsync() throws ExternalDependencyException;
}
