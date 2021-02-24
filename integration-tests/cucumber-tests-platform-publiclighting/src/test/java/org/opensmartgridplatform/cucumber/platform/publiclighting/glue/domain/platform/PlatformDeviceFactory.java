/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.domain.platform;

import java.util.HashMap;
import java.util.Map;

import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.domain.protocol.ProtocolDeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlatformDeviceFactory implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolDeviceFactory.class);

    @Autowired
    LmdDeviceCreator lmdCreator;

    @Autowired
    LmgDeviceCreator lmgCreator;

    private final Map<DeviceType, PlatformDeviceCreator<?>> factoryMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.factoryMap.put(DeviceType.LIGHT_MEASUREMENT_DEVICE, this.lmdCreator);
        this.factoryMap.put(DeviceType.LIGHT_MEASUREMENT_GATEWAY, this.lmgCreator);
    }

    public Object createPlatformDevice(final DeviceType deviceType, final Protocol protocol,
            final Map<String, String> settings) {

        if (this.factoryMap.containsKey(deviceType)) {
            return this.factoryMap.get(deviceType).apply(protocol, settings);
        } else {
            LOGGER.warn("Unsupported DeviceType: {}", deviceType);
            return null;
        }
    }
}
