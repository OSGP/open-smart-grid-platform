/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DefinableLoadProfileConfigurationData;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class DefinableLoadProfileConfigurationDataFactory {

    public static DefinableLoadProfileConfigurationData fromParameterMap(final Map<String, String> parameters) {
        final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData = new DefinableLoadProfileConfigurationData();
        definableLoadProfileConfigurationData.setCaptureObjects(CaptureObjectsFactory.fromParameterMap(parameters));
        definableLoadProfileConfigurationData
                .setCapturePeriod(Helpers.getLong(parameters, PlatformSmartmeteringKeys.CAPTURE_PERIOD, null));
        return definableLoadProfileConfigurationData;
    }
}
