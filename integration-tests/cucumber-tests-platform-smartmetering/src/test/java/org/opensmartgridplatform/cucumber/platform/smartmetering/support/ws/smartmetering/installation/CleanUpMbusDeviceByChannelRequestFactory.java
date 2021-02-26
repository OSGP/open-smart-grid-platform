/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class CleanUpMbusDeviceByChannelRequestFactory {

    private CleanUpMbusDeviceByChannelRequestFactory() {
        // Private constructor for utility class.
    }

    public static CleanUpMbusDeviceByChannelRequest fromSettings(final Map<String, String> settings) {
        final CleanUpMbusDeviceByChannelRequest request = new CleanUpMbusDeviceByChannelRequest();
        final CleanUpMbusDeviceByChannelRequestData requestData = new CleanUpMbusDeviceByChannelRequestData();
        requestData.setChannel(Short.valueOf(settings.get(PlatformKeys.KEY_CHANNEL)));
        request.setCleanUpMbusDeviceByChannelRequestData(requestData);
        request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static CleanUpMbusDeviceByChannelAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final CleanUpMbusDeviceByChannelAsyncRequest asyncRequest = new CleanUpMbusDeviceByChannelAsyncRequest();
        asyncRequest.setCorrelationUid(correlationUid);
        asyncRequest.setDeviceIdentification(deviceIdentification);
        return asyncRequest;
    }

}
