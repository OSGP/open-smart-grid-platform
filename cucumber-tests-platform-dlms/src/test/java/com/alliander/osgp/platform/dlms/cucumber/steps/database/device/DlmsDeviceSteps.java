/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

public abstract class DlmsDeviceSteps {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private DeviceRepository coreDeviceRepository;

    DlmsDevice dlmsDevice = null;
    Device coreDevice = null;

    public void createDlmsDevice(final Map<String, String> inputSettings) {
        final DlmsDeviceBuilder dlmsBuilder = new DlmsDeviceBuilder(inputSettings);
        this.dlmsDevice = dlmsBuilder.buildDlmsDevice(inputSettings);
        this.dlmsDeviceRepository.save(this.dlmsDevice);

        final CoreDeviceBuilder coreBuilder = new CoreDeviceBuilder(inputSettings);
        this.coreDevice = coreBuilder.buildCoreDevice();
        this.coreDeviceRepository.save(this.coreDevice);
    }
}
