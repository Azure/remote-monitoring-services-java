// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models.actions;

import com.google.inject.ImplementedBy;

import java.util.TreeMap;

@ImplementedBy(EmailActionSettings.class)
public interface IActionSettings {
    ActionType getType();

    TreeMap getSettings();
}
