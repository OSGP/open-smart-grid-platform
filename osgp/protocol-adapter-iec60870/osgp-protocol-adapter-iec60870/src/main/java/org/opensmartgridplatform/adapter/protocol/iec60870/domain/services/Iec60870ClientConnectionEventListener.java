/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.io.EOFException;
import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseInfo;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerNotFoundException;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870ClientConnectionEventListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ConnectionEventListener.class);

    private final ConnectionInfo connectionInfo;
    private final ResponseInfo responseInfo;
    private final ClientConnectionCache connectionCache;
    private final ClientAsduHandlerRegistry asduHandlerRegistry;

    public Iec60870ClientConnectionEventListener(final ConnectionInfo connectionInfo,
            final ClientConnectionCache connectionCache, final ClientAsduHandlerRegistry asduHandlerRegistry,
            final ResponseInfo responseInfo) {
        this.connectionInfo = connectionInfo;
        this.connectionCache = connectionCache;
        this.asduHandlerRegistry = asduHandlerRegistry;
        this.responseInfo = responseInfo;
    }

    @Override
    public void newASdu(final ASdu asdu) {
        try {
            final TypeId typeId = asdu.getTypeIdentification();
            final ClientAsduHandler aSduHandler = this.asduHandlerRegistry.getHandler(typeId);
            aSduHandler.handleAsdu(asdu, this.responseInfo);

        } catch (final Iec60870ASduHandlerNotFoundException e) {
            LOGGER.error("Unknown request received, no handler available for ASdu: {}", asdu.toString(), e);
        } catch (final EOFException e) {
            LOGGER.error("Connection closed on connection with device ({}).",
                    this.connectionInfo.getDeviceIdentification(), e);
        } catch (final Exception e) {
            LOGGER.error("Exception occurred on connection with device ({}).",
                    this.connectionInfo.getDeviceIdentification(), e);
        }
    }

    @Override
    public void connectionClosed(final IOException e) {
        final String deviceIdentification = this.connectionInfo.getDeviceIdentification();
        LOGGER.info("Connection with device ({}) closed.", deviceIdentification, e);
        this.connectionCache.removeConnection(deviceIdentification);
    }
}
