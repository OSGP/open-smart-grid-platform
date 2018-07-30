/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DisableDebuggingRequestFactory {
    private DisableDebuggingRequestFactory() {
        // Private constructor for utility class
    }

    public static DisableDebuggingRequest fromParameterMap(final Map<String, String> requestParameters) {
        final DisableDebuggingRequest disableDebuggingRequest = new DisableDebuggingRequest();
        disableDebuggingRequest.setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return disableDebuggingRequest;
    }

    public static DisableDebuggingAsyncRequest fromScenarioContext() {
        final DisableDebuggingAsyncRequest disableDebuggingAsyncRequest = new DisableDebuggingAsyncRequest();
        disableDebuggingAsyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        disableDebuggingAsyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return disableDebuggingAsyncRequest;
    }
}
