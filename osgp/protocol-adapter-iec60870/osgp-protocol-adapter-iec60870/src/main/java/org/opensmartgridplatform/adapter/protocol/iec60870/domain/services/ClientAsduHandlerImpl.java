/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import javax.annotation.PostConstruct;

import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class providing an abstract method for ASdu handling (each type of
 * ASdu will have a specific ASdu Handler implementation) and automatic
 * registration of implementation classes to {@link Iec60870ASduHandlerRegistry}
 *
 */
public abstract class ClientAsduHandlerImpl implements ClientAsduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAsduHandlerImpl.class);

    @Autowired
    private ClientAsduHandlerRegistryImpl iec60870ClientASduHandlerRegistry;

    private TypeId typeId;

    public ClientAsduHandlerImpl(final TypeId typeId) {
        this.typeId = typeId;
    }

    public TypeId getTypeId() {
        return this.typeId;
    }

    @PostConstruct
    protected void register() {
        LOGGER.info("Registering ASdu Handler {}", this.getClass().getSimpleName());
        this.iec60870ClientASduHandlerRegistry.registerHandler(this.typeId, this);
    }
}
