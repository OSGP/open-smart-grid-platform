/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.RequestFactoryHelper;

public class SynchronizeTimeRequestFactory {

    public static SynchronizeTimeRequest fromParameterMap(final Map<String, String> parameters) {
        final SynchronizeTimeRequest request = new SynchronizeTimeRequest();

        request.setDeviceIdentification(
                getString(parameters, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));

        request.setSynchronizeTimeRequestData(SynchronizeTimeRequestDataFactory.fromParameterMap(parameters));

        return request;
    }

    public static SynchronizeTimeAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

        final SynchronizeTimeAsyncRequest request = new SynchronizeTimeAsyncRequest();
        request.setCorrelationUid(correlationUid);
        request.setDeviceIdentification(deviceIdentification);
        return request;
    }
}
