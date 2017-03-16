/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.RequestFactoryHelper;

public class SetClockConfigurationRequestFactory {

    public static SetClockConfigurationRequest fromParameterMap(final Map<String, String> parameters) {
        final SetClockConfigurationRequest request = new SetClockConfigurationRequest();

        request.setDeviceIdentification(
                getString(parameters, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));

        request.setSetClockConfigurationData(SetClockConfigurationRequestDataFactory.fromParameterMap(parameters));

        return request;
    }

    public static SetClockConfigurationAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

        final SetClockConfigurationAsyncRequest request = new SetClockConfigurationAsyncRequest();
        request.setCorrelationUid(correlationUid);
        request.setDeviceIdentification(deviceIdentification);
        return request;
    }
}
