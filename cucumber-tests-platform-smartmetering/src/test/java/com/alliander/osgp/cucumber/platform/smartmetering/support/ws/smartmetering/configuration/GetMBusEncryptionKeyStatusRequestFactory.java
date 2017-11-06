/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMBusEncryptionKeyStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMBusEncryptionKeyStatusRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetMBusEncryptionKeyStatusRequestFactory {
    private GetMBusEncryptionKeyStatusRequestFactory() {
        // Private constructor for utility class
    }

    public static GetMBusEncryptionKeyStatusRequest fromParameterMap(final Map<String, String> requestParameters) {
        final GetMBusEncryptionKeyStatusRequest request = new GetMBusEncryptionKeyStatusRequest();
        request.setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static GetMBusEncryptionKeyStatusAsyncRequest fromScenarioContext() {
        final GetMBusEncryptionKeyStatusAsyncRequest asyncRequest = new GetMBusEncryptionKeyStatusAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }
}
