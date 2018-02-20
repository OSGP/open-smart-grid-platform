/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import com.alliander.osgp.cucumber.core.ReadSettingsHelper;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class ConfigurationObjectFactory {

    private ConfigurationObjectFactory() {
        // Private constructor for utility class
    }

    public static ConfigurationObject fromParameterMap(final Map<String, String> requestParameters) {
        final ConfigurationObject configurationObject = new ConfigurationObject();
        setConfigurationFlags(configurationObject, requestParameters);
        configurationObject.setGprsOperationMode(ReadSettingsHelper.getEnum(requestParameters,
                PlatformSmartmeteringKeys.GPRS_OPERATION_MODE_TYPE, GprsOperationModeType.class));
        return configurationObject;
    }

    private static boolean hasConfigurationFlags(final Map<String, String> requestParameters) {
        return requestParameters.containsKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_COUNT);
    }

    private static void setConfigurationFlags(final ConfigurationObject configurationObject,
            final Map<String, String> requestParameters) {

        if (!hasConfigurationFlags(requestParameters)) {
            return;
        }

        final ConfigurationFlags configurationFlags = new ConfigurationFlags();
        final List<ConfigurationFlag> configurationFlagList = new ArrayList<>();
        final int numberOfFlags = Integer
                .parseInt(requestParameters.get(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_COUNT));
        for (int i = 1; i <= numberOfFlags; i++) {
            configurationFlagList.add(getConfigurationFlag(requestParameters, i));
        }
        configurationFlags.getConfigurationFlag().addAll(configurationFlagList);
        configurationObject.setConfigurationFlags(configurationFlags);
    }

    private static ConfigurationFlag getConfigurationFlag(final Map<String, String> parameters, final int index) {
        final ConfigurationFlag configurationFlag = new ConfigurationFlag();
        configurationFlag.setConfigurationFlagType(getConfigurationFlagType(parameters, index));
        configurationFlag.setEnabled(getConfigurationFlagEnabled(parameters, index));
        return configurationFlag;
    }

    private static ConfigurationFlagType getConfigurationFlagType(final Map<String, String> parameters,
            final int index) {

        final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_TYPE, index);
        return ReadSettingsHelper.getEnum(parameters, key, ConfigurationFlagType.class);
    }

    private static boolean getConfigurationFlagEnabled(final Map<String, String> parameters, final int index) {
        final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_ENABLED, index);
        return ReadSettingsHelper.getBoolean(parameters, key);
    }
}
