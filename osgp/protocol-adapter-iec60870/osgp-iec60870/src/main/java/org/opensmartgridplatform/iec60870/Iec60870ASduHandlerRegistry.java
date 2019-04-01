/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.util.HashMap;
import java.util.Map;

import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * Registry of ASdu handlers, used to look up the corresponding handler for a
 * specific ASdu type. Each ASdu handler should register itself to this
 * registry.
 *
 */
@Component
public class Iec60870ASduHandlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ASduHandlerRegistry.class);

    private Map<TypeId, Iec60870ASduHandler> handlers = new HashMap<>();

    public Iec60870ASduHandler getHandler(final TypeId typeId) throws Iec60870ASduHandlerNotFoundException {
        if (!this.handlers.containsKey(typeId)) {
            LOGGER.error("No ASdu handler found for type Id {}", typeId);
            throw new Iec60870ASduHandlerNotFoundException(typeId);
        }
        return this.handlers.get(typeId);
    }

    public void registerHandler(final TypeId typeId, final Iec60870ASduHandler handler) {
        this.handlers.put(typeId, handler);
    }

}
