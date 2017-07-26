package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DisableDebuggingRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DisableDebuggingRequestFactory {
    private DisableDebuggingRequestFactory() {
        // Private constructor for utility class
    }

    public static DisableDebuggingRequest fromParameterMap(final Map<String, String> requestParameters) {
        final DisableDebuggingRequest request = new DisableDebuggingRequest();
        request.setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static DisableDebuggingAsyncRequest fromScenarioContext() {
        final DisableDebuggingAsyncRequest asyncRequest = new DisableDebuggingAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }
}
