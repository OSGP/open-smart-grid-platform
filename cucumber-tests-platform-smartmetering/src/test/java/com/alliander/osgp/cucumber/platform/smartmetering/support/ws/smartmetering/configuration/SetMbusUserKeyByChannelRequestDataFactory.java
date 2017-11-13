/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequestData;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetMbusUserKeyByChannelRequestDataFactory {
    private SetMbusUserKeyByChannelRequestDataFactory() {
        // Private constructor for utility class
    }

    public static SetMbusUserKeyByChannelRequestData fromParameterMap(final Map<String, String> requestParameters) {
        final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData = new SetMbusUserKeyByChannelRequestData();
        if (!requestParameters.containsKey(PlatformSmartmeteringKeys.CHANNEL)) {
            throw new AssertionError(
                    "A value for key '" + PlatformSmartmeteringKeys.CHANNEL + "' must be set in the step data.");
        }
        setMbusUserKeyByChannelRequestData
                .setChannel(Helpers.getShort(requestParameters, PlatformSmartmeteringKeys.CHANNEL));
        return setMbusUserKeyByChannelRequestData;
    }
}
