/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.RequestFactoryHelper;

public class ReplaceKeysRequestFactory {

    private ReplaceKeysRequestFactory() {
        // Private constructor for utility class.
    }

    public static ReplaceKeysRequest fromParameterMap(final Map<String, String> requestParameters) {
        final ReplaceKeysRequest replaceKeysRequest = new ReplaceKeysRequest();
        replaceKeysRequest.setDeviceIdentification(Helpers.getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        final SetKeysRequestData setKeysRequestData = SetKeysRequestDataFactory.fromParameterMap(requestParameters);
        replaceKeysRequest.setSetKeysRequestData(setKeysRequestData);
        return replaceKeysRequest;
    }

    public static ReplaceKeysAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper
                .getDeviceIdentificationFromStepData(requestParameters);
        final ReplaceKeysAsyncRequest replaceKeysAsyncRequest = new ReplaceKeysAsyncRequest();
        replaceKeysAsyncRequest.setCorrelationUid(correlationUid);
        replaceKeysAsyncRequest.setDeviceIdentification(deviceIdentification);
        return replaceKeysAsyncRequest;
    }
}
