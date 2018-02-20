/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
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

        setConfigurationObjectRequest.setSetConfigurationObjectRequestData(
                SetConfigurationObjectRequestDataFactory.fromParameterMap(requestParameters));

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
}
