/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.mocks.iec60870;

import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandler;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.Iec60870ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870MockServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870MockServer.class);

    private final int port;

    private final int connectionTimeout;

    private Iec60870Server rtuSimulator;

    private Iec60870ASduHandlerRegistry asduHandlerRegistry;

    public Iec60870MockServer(final int port, final int connectionTimeout) {
        this.port = port;
        this.connectionTimeout = connectionTimeout;
    }

    public void start() {

        if (this.isInitialized()) {
            this.asduHandlerRegistry.clearHandlers();

            if (this.rtuSimulator.isListening()) {
                LOGGER.error("RtuSimulator was already started.");
                throw new IllegalStateException("RtuSimulator was already started.");
            }
        } else {
            this.rtuSimulator = this.initializeSimulator();
        }

        this.rtuSimulator.start();
        LOGGER.info("Started IEC60870 Mock server on port {}", this.port);
    }

    public void stop() {

        if (!this.isInitialized() || !this.rtuSimulator.isListening()) {
            LOGGER.warn("Not stopping IEC60870 Mock server, because it was not running.");
            return;
        }

        this.rtuSimulator.stop();
        LOGGER.info("Stopped IEC60870 Mock server");
    }

    public void addIec60870ASduHandler(final ASduType asduType, final Iec60870ASduHandler handler) {
        this.asduHandlerRegistry.registerHandler(asduType, handler);
    }

    private boolean isInitialized() {
        return this.rtuSimulator != null;
    }

    private Iec60870Server initializeSimulator() {
        LOGGER.info("Initialize simulator");
        final Iec60870ConnectionRegistry connectionRegistry = new Iec60870ConnectionRegistry();
        this.asduHandlerRegistry = new Iec60870ASduHandlerRegistry();

        final Iec60870ServerEventListener serverEventListener = new Iec60870ServerEventListener(connectionRegistry,
                this.asduHandlerRegistry, this.connectionTimeout);

        return new Iec60870Server(serverEventListener, this.port);
    }

}
