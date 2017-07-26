/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EnableDebuggingRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class EnableDebuggingRequestFactory {
    private EnableDebuggingRequestFactory() {
        // Private constructor for utility class
    }

    public static EnableDebuggingRequest fromParameterMap(final Map<String, String> requestParameters) {
        final EnableDebuggingRequest request = new EnableDebuggingRequest();
        request.setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static EnableDebuggingAsyncRequest fromScenarioContext() {
        final EnableDebuggingAsyncRequest asyncRequest = new EnableDebuggingAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }
}
