/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.RawMessageData;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;

public class DlmsConnectionHolder implements AutoCloseable {

    private static final DlmsMessageListener DO_NOTHING_LISTENER = new DlmsMessageListener() {

        @Override
        public void messageCaptured(final RawMessageData rawMessageData) {
            // Do nothing.
        }

        @Override
        public void setMessageMetadata(final MessageMetadata messageMetadata) {
            // Do nothing.
        }

        @Override
        public void setDescription(final String description) {
            // Do nothing.
        }
    };

    private final DlmsConnector connector;
    private final DlmsDevice device;
    private final DlmsMessageListener dlmsMessageListener;

    private DlmsConnection dlmsConnection;

    public DlmsConnectionHolder(final DlmsConnector connector, final DlmsDevice device,
            final DlmsMessageListener dlmsMessageListener) {
        this.connector = connector;
        this.device = device;
        if (dlmsMessageListener == null) {
            this.dlmsMessageListener = DO_NOTHING_LISTENER;
        } else {
            this.dlmsMessageListener = dlmsMessageListener;
        }
    }

    public DlmsConnectionHolder(final DlmsConnector connector, final DlmsDevice device) {
        this(connector, device, null);
    }

    /**
     * Returns the current connection, obtained by calling {@link #connect() connect}.
     *
     * @throws IllegalStateException
     *             when there is no connection available.
     * @return
     */
    public DlmsConnection getConnection() {
        if (!this.isConnected()) {
            throw new IllegalStateException("There is no connection available.");
        }
        return this.dlmsConnection;
    }

    public boolean hasDlmsMessageListener() {
        return DO_NOTHING_LISTENER != this.dlmsMessageListener;
    }

    public DlmsMessageListener getDlmsMessageListener() {
        return this.dlmsMessageListener;
    }

    /**
     * Disconnects from the device, and releases the internal connection reference.
     *
     * @throws IOException
     *             When an exception occurs while disconnecting.
     */
    public void disconnect() throws IOException {
        if (this.dlmsConnection != null) {
            this.dlmsConnection.disconnect();
            this.dlmsConnection = null;
        }
    }

    public boolean isConnected() {
        return this.dlmsConnection != null;
    }

    /**
     * Obtains a new connection with a device. A connection should be obtained before {@link #getConnection()
     * getConnection} is called.
     *
     * @Throws IllegalStateException When there is already a connection set.
     * @throws TechnicalException
     *             When an exceptions occurs while creating the exception.
     * @throws FunctionalException
     */
    public void connect() throws TechnicalException, FunctionalException {
        if (this.dlmsConnection != null) {
            throw new IllegalStateException("Cannot create a new connection because a connection already exists.");
        }

        this.dlmsConnection = this.connector.connect(this.device, this.dlmsMessageListener);
    }

    /**
     * Closes the connection with the device and releases the internal connection reference. The connection will be
     * closed, but no disconnection message will be sent to the device.
     */
    @Override
    public void close() throws Exception {
        this.dlmsConnection.close();
        this.dlmsConnection = null;
    }
}
