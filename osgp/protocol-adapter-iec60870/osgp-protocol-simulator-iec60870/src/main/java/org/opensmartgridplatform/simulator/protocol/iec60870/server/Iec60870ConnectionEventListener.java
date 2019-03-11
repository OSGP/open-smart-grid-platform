/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import java.io.EOFException;
import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870ConnectionEventListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ConnectionEventListener.class);

    private final Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;
    private final Connection connection;
    private final int connectionId;

    public Iec60870ConnectionEventListener(final Connection connection, final int connectionId,
            final Iec60870ASduHandlerRegistry iec60870aSduHandlerRegistry) {
        this.connection = connection;
        this.connectionId = connectionId;
        this.iec60870ASduHandlerRegistry = iec60870aSduHandlerRegistry;
    }

    @Override
    public void newASdu(final ASdu aSdu) {
        try {
            final TypeId typeId = aSdu.getTypeIdentification();
            final Iec60870ASduHandler aSduHandler = this.iec60870ASduHandlerRegistry.getHandler(typeId);
            aSduHandler.handleASdu(this.connection, aSdu);

        } catch (final Iec60870ASduHandlerNotFoundException e) {
            LOGGER.error("Unknown request received, no handler available for ASdu: {}", aSdu.toString(), e);
        } catch (final EOFException e) {
            LOGGER.error("Connection closed on connection ({}).", this.connectionId, e);
        } catch (final Exception e) {
            LOGGER.error("Exception occurred on connection ({}).", this.connectionId, e);
        }
    }

    @Override
    public void connectionClosed(final IOException e) {
        LOGGER.info("Connection ({}) closed.", this.connectionId, e);
    }
}
