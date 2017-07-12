/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SynchronizeTimeRequestFactory {

    private SynchronizeTimeRequestFactory() {
        // Private constructor for utility class
    }

    public static SynchronizeTimeRequest fromParameterMap(final Map<String, String> parameters) {
        final SynchronizeTimeRequest request = new SynchronizeTimeRequest();
        request.setDeviceIdentification(parameters.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));
        request.setSynchronizeTimeRequestData(SynchronizeTimeRequestDataFactory.fromParameterMap(parameters));
        return request;
    }

    public static SynchronizeTimeAsyncRequest fromScenarioContext() {
        final SynchronizeTimeAsyncRequest asyncRequest = new SynchronizeTimeAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }

}
