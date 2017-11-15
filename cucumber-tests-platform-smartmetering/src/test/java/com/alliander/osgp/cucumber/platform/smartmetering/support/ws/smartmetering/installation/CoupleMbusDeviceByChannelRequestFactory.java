/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelRequestData;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class CoupleMbusDeviceByChannelRequestFactory {

    private CoupleMbusDeviceByChannelRequestFactory() {
        // Private constructor for utility class.
    }

    public static CoupleMbusDeviceByChannelRequest fromSettings(final Map<String, String> settings) {
        final CoupleMbusDeviceByChannelRequest request = new CoupleMbusDeviceByChannelRequest();
        final CoupleMbusDeviceByChannelRequestData requestData = new CoupleMbusDeviceByChannelRequestData();
        requestData.setChannel(Short.valueOf(settings.get(PlatformKeys.KEY_CHANNEL)).shortValue());
        request.setCoupleMbusDeviceByChannelRequestData(requestData);
        request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        return request;
    }

    public static CoupleMbusDeviceByChannelAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final CoupleMbusDeviceByChannelAsyncRequest asyncRequest = new CoupleMbusDeviceByChannelAsyncRequest();
        asyncRequest.setCorrelationUid(correlationUid);
        asyncRequest.setDeviceIdentification(deviceIdentification);
        return asyncRequest;
    }

}
