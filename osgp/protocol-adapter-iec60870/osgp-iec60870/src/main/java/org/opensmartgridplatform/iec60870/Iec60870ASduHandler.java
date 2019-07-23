/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class providing an abstract method for ASdu handling (each type of
 * ASdu will have a specific ASdu Handler implementation) and automatic
 * registration of implementation classes to {@link Iec60870ASduHandlerRegistry}
 *
 */
public abstract class Iec60870ASduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ASduHandler.class);

    @Autowired
    private Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;

    private TypeId typeId;

    public Iec60870ASduHandler(final TypeId typeId) {
        this.typeId = typeId;
    }

    public abstract void handleASdu(Connection t, ASdu u) throws IOException;

    public TypeId getTypeId() {
        return this.typeId;
    }

    @PostConstruct
    protected void register() {
        LOGGER.info("Registering ASdu Handler {}", this.getClass().getSimpleName());
        this.iec60870ASduHandlerRegistry.registerHandler(this.typeId, this);
    }
}
