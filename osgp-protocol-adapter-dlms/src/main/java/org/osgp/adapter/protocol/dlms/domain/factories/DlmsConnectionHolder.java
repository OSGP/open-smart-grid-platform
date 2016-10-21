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
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class DlmsConnectionHolder implements AutoCloseable {

    private static final DlmsMessageListener DO_NOTHING_LISTENER = new DlmsMessageListener() {

        @Override
        public void messageCaptured(final RawMessageData rawMessageData) {
            // Do nothing.
        }

        @Override
        public void setMessageMetadata(final DlmsDeviceMessageMetadata messageMetadata) {
            // Do nothing.
        }

        @Override
        public void setDescription(final String description) {
            // Do nothing.
        }
    };

    private final Hls5Connector connector;
    private final DlmsDevice device;
    private final DlmsMessageListener dlmsMessageListener;

    private DlmsConnection dlmsConnection;

    public DlmsConnectionHolder(final Hls5Connector connector, final DlmsDevice device,
            final DlmsMessageListener dlmsMessageListener) {
        this.connector = connector;
        this.device = device;
        if (dlmsMessageListener == null) {
            this.dlmsMessageListener = DO_NOTHING_LISTENER;
        } else {
            this.dlmsMessageListener = dlmsMessageListener;
        }
    }

    public DlmsConnectionHolder(final Hls5Connector connector, final DlmsDevice device) {
        this(connector, device, null);
    }

    public DlmsConnection getConnection() {
        return this.dlmsConnection;
    }

    public boolean hasDlmsMessageListener() {
        return DO_NOTHING_LISTENER != this.dlmsMessageListener;
    }

    public DlmsMessageListener getDlmsMessageListener() {
        return this.dlmsMessageListener;
    }

    public void disconnect() throws IOException {
        if (this.dlmsConnection != null) {
            this.dlmsConnection.disconnect();
            this.dlmsConnection = null;
        }
    }

    public boolean isConnected() {
        return this.dlmsConnection != null;
    }

    public void connect() throws TechnicalException {
        if (this.dlmsConnection != null) {
            throw new IllegalStateException("Cannot create a new connection because a connection already exists.");
        }

        this.dlmsConnection = connector.connect(device, dlmsMessageListener);
    }

    @Override
    public void close() throws Exception {
        this.disconnect();
    }
}
