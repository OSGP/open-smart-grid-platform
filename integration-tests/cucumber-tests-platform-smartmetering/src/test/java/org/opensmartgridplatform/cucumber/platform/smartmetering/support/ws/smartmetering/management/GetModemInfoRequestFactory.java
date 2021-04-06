/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.DeviceLifecycleStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetModemInfoRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetModemInfoRequestFactory {

    private GetModemInfoRequestFactory() {
        // Private constructor for utility class
    }

    public static GetModemInfoRequest fromParameterMap(final Map<String, String> settings) {
        final GetModemInfoRequest request = new GetModemInfoRequest();
        final GetModemInfoRequestData requestData = new GetModemInfoRequestData();
        request.setDeviceIdentification(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        request.setGetModemInfoRequestData(requestData);
        return request;
    }

    public static GetModemInfoAsyncRequest fromScenarioContext() {
        final GetModemInfoAsyncRequest asyncRequest = new GetModemInfoAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }

}
