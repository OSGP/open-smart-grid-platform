/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.EnumMap;
import java.util.Map;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * Registry of Client Asdu Handlers, used to look up the corresponding handler
 * for a specific Asdu type. Each Client Asdu Handler should register itself to
 * this registry.
 *
 */
@Component
public class ClientAsduHandlerRegistryImpl implements ClientAsduHandlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAsduHandlerRegistryImpl.class);

    private Map<TypeId, ClientAsduHandler> handlers = new EnumMap<>(TypeId.class);

    @Override
    public ClientAsduHandler getHandler(final ASdu asdu) throws Iec60870ASduHandlerNotFoundException {
        final TypeId typeId = asdu.getTypeIdentification();
        final ClientAsduHandler handler = this.handlers.get(typeId);
        if (handler == null) {
            LOGGER.error("No ASdu handler found for type Id {}", typeId);
            throw new Iec60870ASduHandlerNotFoundException(typeId);
        }
        return handler;
    }

    @Override
    public void registerHandler(final TypeId typeId, final ClientAsduHandler clientAsduHandler) {
        this.handlers.put(typeId, clientAsduHandler);
    }
}
