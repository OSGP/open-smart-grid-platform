/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Class implementing the {@link ServerEventListener} interface for incoming
 * connection attempts.
 *
 */
public class Iec60870ServerEventListener implements ServerEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ServerEventListener.class);

    private final Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;
    private final int connectionTimeout;

    private int connectionIdCounter = 1;

    public Iec60870ServerEventListener(final Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry,
            final int connectionTimeout) {
        this.iec60870ASduHandlerRegistry = iec60870ASduHandlerRegistry;
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public void connectionIndication(final Connection connection) {
        final int connectionId = this.connectionIdCounter++;
        LOGGER.info("Client connected on connection ({}).", connectionId);

        try {
            LOGGER.info("Waiting for StartDT on connection ({}) for {} ms.", connectionId, this.connectionTimeout);
            connection.waitForStartDT(
                    new Iec60870ConnectionEventListener(connection, connectionId, this.iec60870ASduHandlerRegistry),
                    this.connectionTimeout);
        } catch (final IOException | TimeoutException e) {
            LOGGER.error("Exception occurred while connection ({}) was waiting for StartDT.", connectionId, e);
            return;
        }

        LOGGER.info("Connection ({}) listening for incoming commands.", connectionId);
    }

    @Override
    public void serverStoppedListeningIndication(final IOException e) {
        LOGGER.info("Server has stopped listening: {}.", e.getMessage());
    }

    @Override
    public void connectionAttemptFailed(final IOException e) {
        LOGGER.warn("Connection attempt failed: {}", e.getMessage());
    }

}
