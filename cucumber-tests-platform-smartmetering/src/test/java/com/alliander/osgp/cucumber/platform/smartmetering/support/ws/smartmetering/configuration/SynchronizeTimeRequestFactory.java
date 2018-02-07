/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SynchronizeTimeRequestFactory {

    public static SynchronizeTimeRequest fromParameterMap(final Map<String, String> parameters) {
        final SynchronizeTimeRequest synchronizeTimeRequest = new SynchronizeTimeRequest();

        synchronizeTimeRequest.setDeviceIdentification(getString(parameters,
                PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

        synchronizeTimeRequest
                .setSynchronizeTimeRequestData(SynchronizeTimeRequestDataFactory.fromParameterMap(parameters));

        return synchronizeTimeRequest;
    }

    public static SynchronizeTimeAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

        final SynchronizeTimeAsyncRequest synchronizeTimeAsyncRequest = new SynchronizeTimeAsyncRequest();
        synchronizeTimeAsyncRequest.setCorrelationUid(correlationUid);
        synchronizeTimeAsyncRequest.setDeviceIdentification(deviceIdentification);
        return synchronizeTimeAsyncRequest;
    }
}
