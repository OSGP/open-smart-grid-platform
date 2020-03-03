/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.io.IOException;

import org.openmuc.j60870.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Server.class);

    private final Iec60870ServerEventListener iec60870ServerEventListener;
    private Server server;
    private final int port;
    private boolean listening = false;

    public Iec60870Server(final Iec60870ServerEventListener iec60870ServerEventListener, final int port) {
        this.iec60870ServerEventListener = iec60870ServerEventListener;
        this.port = port;
    }

    public void start() {
        this.server = Server.builder().setPort(this.port).build();

        try {
            LOGGER.info("Starting IEC60870 Server on port {}.", this.port);
            this.server.start(this.iec60870ServerEventListener);
            this.listening = true;
            LOGGER.info("Started IEC60870 Server.");
        } catch (final IOException e) {
            LOGGER.error("Exception occurred while starting IEC60870 server.", e);
        }
    }

    public void stop() {
        LOGGER.info("Stopping IEC60870 Server on port {}.", this.port);
        this.server.stop();
        this.listening = false;
        LOGGER.info("Stopped IEC60870 Server.");
    }

    public Iec60870ServerEventListener getIec60870ServerEventListener() {
        return this.iec60870ServerEventListener;
    }

    public boolean isListening() {
        return this.listening;
    }
}
