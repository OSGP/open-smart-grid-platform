/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.EnumMap;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClientAsduHandlerRegistryMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAsduHandlerRegistryMap.class);

    private final EnumMap<DeviceType, ClientAsduHandlerRegistry> map = new EnumMap<>(DeviceType.class);

    public ClientAsduHandlerRegistry forDeviceType(final DeviceType deviceType) {
        LOGGER.debug("Retrieving client asdu handler for device type {}", deviceType);

        return this.map.get(deviceType);
    }

    public void addClientAsduHandlerRegistry(final DeviceType deviceType, final ClientAsduHandlerRegistry registry) {
        LOGGER.info("Adding client asdu handler registry for device type {}", deviceType);
        this.map.put(deviceType, registry);
    }
}
