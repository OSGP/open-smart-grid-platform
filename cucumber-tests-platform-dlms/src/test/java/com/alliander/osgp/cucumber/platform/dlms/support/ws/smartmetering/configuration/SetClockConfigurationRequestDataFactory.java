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

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequestData;
import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;

public class SetClockConfigurationRequestDataFactory {

    public static SetClockConfigurationRequestData fromParameterMap(final Map<String, String> parameters) {
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

        return setClockConfigurationData;
    }
}
