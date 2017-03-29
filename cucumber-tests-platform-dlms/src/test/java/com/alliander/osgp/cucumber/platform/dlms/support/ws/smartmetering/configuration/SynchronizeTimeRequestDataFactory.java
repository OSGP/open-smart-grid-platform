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

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData;
import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;

public class SynchronizeTimeRequestDataFactory {

    public static SynchronizeTimeRequestData fromParameterMap(final Map<String, String> parameters) {
        final SynchronizeTimeRequestData requestData = new SynchronizeTimeRequestData();

        requestData.setDeviation(getByte(parameters, Keys.DEVIATION, Defaults.DEVIATION));
        requestData.setDst(getBoolean(parameters, Keys.DAYLIGHT_SAVINGS_ACTIVE, Defaults.DAYLIGHT_SAVINGS_ACTIVE));

        return requestData;
    }
}
