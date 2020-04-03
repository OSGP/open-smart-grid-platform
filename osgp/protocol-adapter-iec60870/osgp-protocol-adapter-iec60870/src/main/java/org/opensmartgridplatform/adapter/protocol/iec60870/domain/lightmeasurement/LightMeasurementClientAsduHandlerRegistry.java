/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistryMap;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * Registry of Client Asdu Handlers, used to look up the corresponding handler
 * for a specific Asdu type. Each Client Asdu Handler should register itself to
 * this registry.
 *
 */
@Component
public class LightMeasurementClientAsduHandlerRegistry implements ClientAsduHandlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightMeasurementClientAsduHandlerRegistry.class);

    private static final DeviceType DEVICE_TYPE = DeviceType.LIGHT_MEASUREMENT_GATEWAY;

    @Autowired
    private ClientAsduHandlerRegistryMap clientAsduHandlerRegistryMap;

    private final Map<ASduType, ClientAsduHandler> handlers = new EnumMap<>(ASduType.class);

    @Override
    public ClientAsduHandler getHandler(final ASdu asdu) throws Iec60870ASduHandlerNotFoundException {
        final ASduType asduType = asdu.getTypeIdentification();
        final ClientAsduHandler handler = this.handlers.get(asduType);
        if (handler == null) {
            LOGGER.error("No Asdu handler found for Asdu type {}", asduType);
            throw new Iec60870ASduHandlerNotFoundException(asduType);
        }
        return handler;
    }

    @Override
    public void registerHandler(final ASduType asduType, final ClientAsduHandler clientAsduHandler) {
        this.handlers.put(asduType, clientAsduHandler);
    }

    @PostConstruct
    public void addToRegistryMap() {
        LOGGER.info("Adding asdu handler registry {} for device type {} to map.", this.getClass().getSimpleName(),
                DEVICE_TYPE);
        this.clientAsduHandlerRegistryMap.addClientAsduHandlerRegistry(DEVICE_TYPE, this);
    }
}
