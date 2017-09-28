/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.math.BigInteger;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequestData;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetPushSetupSmsRequestFactory {

    private SetPushSetupSmsRequestFactory() {
        // Private constructor for utility class
    }

    public static SetPushSetupSmsRequest fromParameterMap(final Map<String, String> settings) {
        final SetPushSetupSmsRequest request = new SetPushSetupSmsRequest();
        request.setDeviceIdentification(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

        request.setSetPushSetupSmsRequestData(createRequestData(settings));
        return request;
    }

    public static SetPushSetupSmsAsyncRequest fromScenarioContext() {
        final SetPushSetupSmsAsyncRequest asyncRequest = new SetPushSetupSmsAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }

    private static SetPushSetupSmsRequestData createRequestData(final Map<String, String> settings) {

        final PushSetupSms sms = new PushSetupSms();
        sms.setHost(settings.get(PlatformSmartmeteringKeys.HOSTNAME));
        sms.setPort(BigInteger.valueOf(Long.valueOf(settings.get(PlatformSmartmeteringKeys.PORT))));

        final SetPushSetupSmsRequestData requestData = new SetPushSetupSmsRequestData();
        requestData.setPushSetupSms(sms);

        return requestData;
    }

}
