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
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

public abstract class DlmsDeviceSteps {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private DlmsSecurityKeyRepository securityKeyRepository;

    @Autowired
    private DeviceRepository coreDeviceRepository;

    DlmsDevice dlmsDevice = null;
    SecurityKey securityKey = null;
    Device coreDevice = null;

    public void createDlmsDevice(final Map<String, String> inputSettings) {
        final DlmsDeviceBuilder dlmsDeviceBuilder = new DlmsDeviceBuilder().buildDlmsDevice(inputSettings);

        // Access the builder for security keys.
        // dlmsDeviceBuilder.getAuthenticationSecurityKeyBuilder().setKey("").setValidFrom(new
        // Date());
        dlmsDeviceBuilder.getAuthenticationSecurityKeyBuilder().buildSecurityKey(inputSettings);

        this.dlmsDevice = dlmsDeviceBuilder.build();
        this.dlmsDeviceRepository.save(this.dlmsDevice);

        final CoreDeviceBuilder coreBuilder = new CoreDeviceBuilder(inputSettings);
        this.coreDevice = coreBuilder.buildCoreDevice();
        this.coreDeviceRepository.save(this.coreDevice);
    }
}
