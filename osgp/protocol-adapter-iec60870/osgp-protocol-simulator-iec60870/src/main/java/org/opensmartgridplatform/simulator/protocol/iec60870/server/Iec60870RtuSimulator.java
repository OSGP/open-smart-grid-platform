/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import java.io.IOException;

import org.openmuc.j60870.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870RtuSimulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870RtuSimulator.class);

    private Iec60870ServerEventListener iec60870ServerEventListener;
    private Server server;

    public Iec60870RtuSimulator(final Iec60870ServerEventListener iec60870ServerEventListener) {
        this.iec60870ServerEventListener = iec60870ServerEventListener;
    }

    public void start() {
        this.server = new Server.Builder().build();

        try {
            LOGGER.info("Starting IEC60870 Server.");
            this.server.start(this.iec60870ServerEventListener);
            LOGGER.info("Started IEC60870 Server.");
        } catch (final IOException e) {
            LOGGER.error("Exception occurred while starting IEC60870 server.", e);
        }
    }

    public void stop() {
        LOGGER.info("Stopping IEC60870 Server.");
        this.server.stop();
        LOGGER.info("Stopped IEC60870 Server.");
    }

}
