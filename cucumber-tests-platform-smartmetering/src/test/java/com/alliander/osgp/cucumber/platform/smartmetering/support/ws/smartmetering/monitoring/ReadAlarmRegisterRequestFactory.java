/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ReadAlarmRegisterRequestFactory {

    private ReadAlarmRegisterRequestFactory() {
        // Private constructor for utility class
    }

    public static ReadAlarmRegisterRequest fromParameterMap(final Map<String, String> requestParameters) {
        final ReadAlarmRegisterRequest readAlarmRegisterRequest = new ReadAlarmRegisterRequest();
        readAlarmRegisterRequest.setDeviceIdentification(requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        return readAlarmRegisterRequest;
    }

    public static ReadAlarmRegisterAsyncRequest fromScenarioContext() {
        final ReadAlarmRegisterAsyncRequest readAlarmRegisterAsyncRequest = new ReadAlarmRegisterAsyncRequest();
        readAlarmRegisterAsyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        readAlarmRegisterAsyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return readAlarmRegisterAsyncRequest;
    }
}
