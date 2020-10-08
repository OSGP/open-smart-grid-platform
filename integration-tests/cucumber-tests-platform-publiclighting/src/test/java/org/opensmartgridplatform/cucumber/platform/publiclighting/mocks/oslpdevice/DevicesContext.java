/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DevicesContext {

    private final ConcurrentMap<String, DeviceState> mockedDevicesMap = new ConcurrentHashMap<>();

    public DeviceState getDeviceState(final String deviceIdentification) {
        DeviceState deviceState = this.mockedDevicesMap.get(deviceIdentification);
        if (deviceState == null) {
            deviceState = new DeviceState(deviceIdentification);
            this.mockedDevicesMap.put(deviceIdentification, deviceState);
        }

        return deviceState;
    }

    public void clear() {
        this.mockedDevicesMap.clear();
    }
}
