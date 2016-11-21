/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.util.Map;

import com.alliander.osgp.domain.core.entities.Device;

public class CoreDeviceBuilder implements DeviceBuilder {

    private final Map<String, String> inputSettings;

    private final String deviceId = null;

    CoreDeviceBuilder(final Map<String, String> inputSettings) {
        this.inputSettings = inputSettings;
    }

    public Device buildCoreDevice() {
        final Device coreDevice = new Device();
        return coreDevice;
    }

    @Override
    public DlmsDeviceBuilder setDeviceId(final String deviceId) {
        // TODO Auto-generated method stub
        return null;
    }
}
