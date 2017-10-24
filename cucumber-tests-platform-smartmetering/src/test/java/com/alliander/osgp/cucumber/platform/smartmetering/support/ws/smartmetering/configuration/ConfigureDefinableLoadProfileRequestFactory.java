/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ConfigureDefinableLoadProfileRequestFactory {

    public static ConfigureDefinableLoadProfileRequest fromParameterMap(final Map<String, String> parameters) {
        final ConfigureDefinableLoadProfileRequest configureDefinableLoadProfileRequest = new ConfigureDefinableLoadProfileRequest();

        configureDefinableLoadProfileRequest.setDeviceIdentification(getString(parameters,
                PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

        configureDefinableLoadProfileRequest.setDefinableLoadProfileConfigurationData(
                DefinableLoadProfileConfigurationDataFactory.fromParameterMap(parameters));

        return configureDefinableLoadProfileRequest;
    }

    public static ConfigureDefinableLoadProfileAsyncRequest fromParameterMapAsync(
            final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

        final ConfigureDefinableLoadProfileAsyncRequest configureDefinableLoadProfileAsyncRequest = new ConfigureDefinableLoadProfileAsyncRequest();
        configureDefinableLoadProfileAsyncRequest.setCorrelationUid(correlationUid);
        configureDefinableLoadProfileAsyncRequest.setDeviceIdentification(deviceIdentification);
        return configureDefinableLoadProfileAsyncRequest;
    }
}
