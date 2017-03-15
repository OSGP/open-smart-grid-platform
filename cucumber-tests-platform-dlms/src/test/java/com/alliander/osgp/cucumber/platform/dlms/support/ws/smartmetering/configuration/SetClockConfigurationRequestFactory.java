/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getByte;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getHexDecoded;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getShort;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequestData;
import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.RequestFactoryHelper;

public class SetClockConfigurationRequestFactory {

    public static SetClockConfigurationRequest fromParameterMap(final Map<String, String> parameters) {
        final SetClockConfigurationRequest request = new SetClockConfigurationRequest();

        request.setDeviceIdentification(
                getString(parameters, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));

        final SetClockConfigurationRequestData setClockConfigurationData = new SetClockConfigurationRequestData();
        setClockConfigurationData.setDaylightSavingsBegin(
                getHexDecoded(parameters, Keys.DAYLIGHT_SAVINGS_BEGIN, Defaults.DAYLIGHT_SAVINGS_BEGIN));
        setClockConfigurationData.setDaylightSavingsEnd(
                getHexDecoded(parameters, Keys.DAYLIGHT_SAVINGS_END, Defaults.DAYLIGHT_SAVINGS_END));
        setClockConfigurationData.setDaylightSavingsDeviation(
                getByte(parameters, Keys.DAYLIGHT_SAVINGS_DEVIATION, Defaults.DAYLIGHT_SAVINGS_DEVIATION));
        setClockConfigurationData.setDaylightSavingsEnabled(
                getBoolean(parameters, Keys.DAYLIGHT_SAVINGS_ENABLED, Defaults.DAYLIGHT_SAVINGS_ENABLED));
        setClockConfigurationData
                .setTimeZoneOffset(getShort(parameters, Keys.TIME_ZONE_OFFSET, Defaults.TIME_ZONE_OFFSET));

        request.setSetClockConfigurationData(setClockConfigurationData);

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
