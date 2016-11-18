/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class CoreDeviceBuilder implements DeviceBuilder {

    private final Map<String, String> inputSettings;

    private String deviceId = null;

    CoreDeviceBuilder(final Map<String, String> inputSettings) {
        this.inputSettings = inputSettings;
    }

    @Override
    public DeviceBuilder setDeviceId() {
        this.deviceId = getString(this.inputSettings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        return this;
    }

    public Device buildCoreDevice() {
        final Device coreDevice = new Device();
        return coreDevice;
    }
}
