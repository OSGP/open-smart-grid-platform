/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeviceModel;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

public class DeviceModelFactory {

    private DeviceModelFactory() {
        // Private constructor for utility class.
    }

    public static DeviceModel fromParameterMap(final Map<String, String> settings) {
        final DeviceModel deviceModel = new DeviceModel();

        deviceModel.setManufacturer(Helpers.getString(settings, PlatformKeys.MANUFACTURER_CODE,
                PlatformDefaults.DEFAULT_MANUFACTURER_CODE));
        deviceModel.setModelCode(Helpers.getString(settings, PlatformKeys.DEVICEMODEL_MODELCODE,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));

        return deviceModel;
    }
}
