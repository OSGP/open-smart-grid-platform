/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import javax.annotation.PostConstruct;

import org.openmuc.j60870.ASduType;
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

    private final ASduType asduType;

    public ClientAsduHandlerImpl(final ASduType asduType) {
        this.asduType = asduType;
    }

    public ASduType getAsduType() {
        return this.asduType;
    }

    @PostConstruct
    protected void register() {
        LOGGER.info("Registering ASdu Handler {}", this.getClass().getSimpleName());
        this.iec60870ClientASduHandlerRegistry.registerHandler(this.asduType, this);
    }
}
