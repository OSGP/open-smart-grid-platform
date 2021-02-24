/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.domain.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol.ProtocolType;
import org.opensmartgridplatform.cucumber.protocol.iec60870.database.Iec60870DeviceDbFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProtocolDeviceFactory implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolDeviceFactory.class);

    @Autowired
    Iec60870DeviceDbFactory iec60870DeviceDbFactory;

    private final Map<ProtocolType, BiFunction<DeviceType, Map<String, String>, Object>> factoryMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.factoryMap.put(ProtocolType.IEC60870, this::createIec60870Device);
    }

    public Object createProtocolDevice(final DeviceType deviceType, final Protocol protocol,
            final Map<String, String> settings) {

        final ProtocolType protocolType = protocol.getType();

        if (this.factoryMap.containsKey(protocolType)) {
            return this.factoryMap.get(protocolType).apply(deviceType, settings);
        } else {
            LOGGER.warn("Unsupported protocol: {}", protocol);
            return null;
        }
    }

    private Iec60870Device createIec60870Device(final DeviceType deviceType, final Map<String, String> settings) {
        return this.iec60870DeviceDbFactory.create(deviceType, settings);
    }
}
