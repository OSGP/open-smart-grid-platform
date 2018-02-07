/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getBoolean;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetConfigurationObjectRequestBuilder {

    private static final GprsOperationModeType DEFAULT_GPRS_OPERATION_MODE_TYPE = GprsOperationModeType.ALWAYS_ON;
    private static final int DEFAULT_CONFIGURATION_FLAG_COUNT = 1;
    private static final ConfigurationFlagType DEFAULT_CONFIGURATION_FLAG_TYPE = ConfigurationFlagType.DISCOVER_ON_POWER_ON;
    private static final boolean DEFAULT_CONFIGURATION_FLAG_ENABLED = true;

    private GprsOperationModeType gprsOperationModeType;
    private List<ConfigurationFlag> configurationFlags = new ArrayList<>();

    public SetConfigurationObjectRequestBuilder withDefaults() {
        this.gprsOperationModeType = DEFAULT_GPRS_OPERATION_MODE_TYPE;
        this.configurationFlags = new ArrayList<>();
        this.configurationFlags.add(this.getDefaultConfigurationFlag());
        return this;
    }

    public SetConfigurationObjectRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.gprsOperationModeType = this.getGprsOperationModeType(parameters);
        this.configurationFlags = new ArrayList<>();
        final int configurationFlagCount = this.getConfigurationFlagCount(parameters);
        for (int i = 1; i <= configurationFlagCount; i++) {
            this.configurationFlags.add(this.getConfigurationFlag(parameters, i));
        }
        return this;
    }

    public SetConfigurationObjectRequest build() {
        final ConfigurationFlags configurationFlags = new ConfigurationFlags();
        configurationFlags.getConfigurationFlag().addAll(this.configurationFlags);
        final ConfigurationObject configurationObject = new ConfigurationObject();
        configurationObject.setGprsOperationMode(this.gprsOperationModeType);
        configurationObject.setConfigurationFlags(configurationFlags);
        final SetConfigurationObjectRequest request = new SetConfigurationObjectRequest();
        request.setConfigurationObject(configurationObject);
        return request;
    }

    private GprsOperationModeType getGprsOperationModeType(final Map<String, String> parameters) {
        return getEnum(parameters, PlatformSmartmeteringKeys.GPRS_OPERATION_MODE_TYPE, GprsOperationModeType.class,
                DEFAULT_GPRS_OPERATION_MODE_TYPE);
    }

    private int getConfigurationFlagCount(final Map<String, String> parameters) {
        return getInteger(parameters, PlatformSmartmeteringKeys.CONFIGURATION_FLAG_COUNT,
                DEFAULT_CONFIGURATION_FLAG_COUNT);
    }

    private ConfigurationFlag getDefaultConfigurationFlag() {
        final ConfigurationFlag configurationFlag = new ConfigurationFlag();
        configurationFlag.setConfigurationFlagType(DEFAULT_CONFIGURATION_FLAG_TYPE);
        configurationFlag.setEnabled(DEFAULT_CONFIGURATION_FLAG_ENABLED);
        return configurationFlag;
    }

    private ConfigurationFlag getConfigurationFlag(final Map<String, String> parameters, final int index) {
        final ConfigurationFlag configurationFlag = new ConfigurationFlag();
        configurationFlag.setConfigurationFlagType(this.getConfigurationFlagType(parameters, index));
        configurationFlag.setEnabled(this.getConfigurationFlagEnabled(parameters, index));
        return configurationFlag;
    }

    private ConfigurationFlagType getConfigurationFlagType(final Map<String, String> parameters, final int index) {
        final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_TYPE, index);
        return getEnum(parameters, key, ConfigurationFlagType.class, DEFAULT_CONFIGURATION_FLAG_TYPE);
    }

    private boolean getConfigurationFlagEnabled(final Map<String, String> parameters, final int index) {
        final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_ENABLED, index);
        return getBoolean(parameters, key, DEFAULT_CONFIGURATION_FLAG_ENABLED);
    }

}
