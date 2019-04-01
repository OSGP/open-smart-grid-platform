/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.io.EOFException;
import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Class implementing the {@link ConnectionEventListener} interface for
 * receiving incoming ASdus and connection closed events.
 *
 */
public class Iec60870ConnectionEventListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ConnectionEventListener.class);

    private final int connectionId;
    private final Connection connection;
    private final Iec60870ConnectionRegistry iec60870ConnectionRegistry;
    private final Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry;

    public Iec60870ConnectionEventListener(final Connection connection, final int connectionId,
            final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
            final Iec60870ASduHandlerRegistry iec60870aSduHandlerRegistry) {
        this.connectionId = connectionId;
        this.connection = connection;
        this.iec60870ConnectionRegistry = iec60870ConnectionRegistry;
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
        this.iec60870ConnectionRegistry.unregisterConnection(this.connectionId);
    }
}
