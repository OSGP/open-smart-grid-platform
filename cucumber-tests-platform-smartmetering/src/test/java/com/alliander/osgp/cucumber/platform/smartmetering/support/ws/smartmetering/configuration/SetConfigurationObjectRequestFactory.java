/**
 * Copyright 2017 Smart Society Services B.V.
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
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetConfigurationObjectRequestFactory {
    private SetConfigurationObjectRequestFactory() {
        // Private constructor for utility class
    }

    public static SetConfigurationObjectRequest fromParameterMap(final Map<String, String> requestParameters) {
        final SetConfigurationObjectRequest setConfigurationObjectRequest = new SetConfigurationObjectRequest();
        setConfigurationObjectRequest
                .setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

        setConfigurationObjectRequest.setSetConfigurationObjectRequestData(fetchConfigurationObject(requestParameters));

        return setConfigurationObjectRequest;
    }

    public static SetConfigurationObjectAsyncRequest fromScenarioContext() {
        final SetConfigurationObjectAsyncRequest setConfigurationObjectAsyncRequest = new SetConfigurationObjectAsyncRequest();
        setConfigurationObjectAsyncRequest
                .setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        setConfigurationObjectAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return setConfigurationObjectAsyncRequest;
    }

    private static SetConfigurationObjectRequestData fetchConfigurationObject(
            final Map<String, String> requestParameters) {
        final ConfigurationObject configurationObject = new ConfigurationObject();
        final ConfigurationFlags configurationFlags = new ConfigurationFlags();

        final List<ConfigurationFlag> configurationFlagLst = new ArrayList<>();
        final ConfigurationFlag ConfigurationFlag = new ConfigurationFlag();
        ConfigurationFlag.setConfigurationFlagType(ConfigurationFlagType
                .valueOf(requestParameters.get(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_TYPE)));

        ConfigurationFlag.setEnabled(
                Boolean.parseBoolean(requestParameters.get(PlatformSmartmeteringKeys.CONFIGURATION_FLAG_ENABLED)));
        configurationFlagLst.add(ConfigurationFlag);

        configurationFlags.getConfigurationFlag().addAll(configurationFlagLst);

        configurationObject.setConfigurationFlags(configurationFlags);
        configurationObject.setGprsOperationMode(GprsOperationModeType
                .valueOf(requestParameters.get(PlatformSmartmeteringKeys.GPRS_OPERATION_MODE_TYPE)));

        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData();
        setConfigurationObjectRequestData.setConfigurationObject(configurationObject);

        return setConfigurationObjectRequestData;
    }

}
