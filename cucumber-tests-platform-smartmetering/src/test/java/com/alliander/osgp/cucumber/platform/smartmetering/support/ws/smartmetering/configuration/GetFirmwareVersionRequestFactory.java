/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetFirmwareVersionRequestFactory {
    private GetFirmwareVersionRequestFactory() {
        // Private constructor for utility class
    }

    public static GetFirmwareVersionRequest fromParameterMap(final Map<String, String> requestParameters) {
        final GetFirmwareVersionRequest getFirmwareVersionRequest = new GetFirmwareVersionRequest();
        getFirmwareVersionRequest
                .setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return getFirmwareVersionRequest;
    }

    public static GetFirmwareVersionAsyncRequest fromScenarioContext() {
        final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest = new GetFirmwareVersionAsyncRequest();
        getFirmwareVersionAsyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        getFirmwareVersionAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return getFirmwareVersionAsyncRequest;
    }
}
