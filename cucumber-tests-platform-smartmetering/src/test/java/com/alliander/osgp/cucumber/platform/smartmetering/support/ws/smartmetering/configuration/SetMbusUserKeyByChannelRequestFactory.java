/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequestData;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetMbusUserKeyByChannelRequestFactory {
    private SetMbusUserKeyByChannelRequestFactory() {
        // Private constructor for utility class
    }

    public static SetMbusUserKeyByChannelRequest fromParameterMap(final Map<String, String> requestParameters) {
        final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest = new SetMbusUserKeyByChannelRequest();
        setMbusUserKeyByChannelRequest
                .setDeviceIdentification(requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
        final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData = SetMbusUserKeyByChannelRequestDataFactory
                .fromParameterMap(requestParameters);
        setMbusUserKeyByChannelRequest.setSetMbusUserKeyByChannelRequestData(setMbusUserKeyByChannelRequestData);
        return setMbusUserKeyByChannelRequest;
    }

    public static SetMbusUserKeyByChannelAsyncRequest fromScenarioContext() {
        final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest = new SetMbusUserKeyByChannelAsyncRequest();
        setMbusUserKeyByChannelAsyncRequest
                .setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        setMbusUserKeyByChannelAsyncRequest
                .setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return setMbusUserKeyByChannelAsyncRequest;
    }
}
