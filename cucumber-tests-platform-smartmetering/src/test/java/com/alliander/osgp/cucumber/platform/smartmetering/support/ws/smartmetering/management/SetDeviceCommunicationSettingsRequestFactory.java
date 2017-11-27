/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetDeviceCommunicationSettingsRequestFactory {
    private SetDeviceCommunicationSettingsRequestFactory() {
        // Private constructor for utility class
    }

    public static SetDeviceCommunicationSettingsRequest fromParameterMap(final Map<String, String> requestParameters) {
        final SetDeviceCommunicationSettingsRequest setDeviceCommunicationSettingsRequest = new SetDeviceCommunicationSettingsRequest();
        setDeviceCommunicationSettingsRequest
                .setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

        setDeviceCommunicationSettingsRequest.setSetDeviceCommunicationSettingsData(
                SetDeviceCommunicationSettingsRequestDataFactory.fromParameterMap(requestParameters));

        return setDeviceCommunicationSettingsRequest;
    }

    public static SetDeviceCommunicationSettingsAsyncRequest fromScenarioContext() {
        final SetDeviceCommunicationSettingsAsyncRequest setDeviceCommunicationSettingsAsyncRequest = new SetDeviceCommunicationSettingsAsyncRequest();
        setDeviceCommunicationSettingsAsyncRequest
                .setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        setDeviceCommunicationSettingsAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

        return setDeviceCommunicationSettingsAsyncRequest;
    }
}
