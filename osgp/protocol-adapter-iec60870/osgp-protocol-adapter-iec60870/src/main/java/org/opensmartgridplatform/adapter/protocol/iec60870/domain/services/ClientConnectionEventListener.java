/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnectionEventListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectionEventListener.class);

    private final ResponseMetadata responseMetadata;
    private final ClientConnectionCache connectionCache;
    private final ClientAsduHandlerRegistry asduHandlerRegistry;
    private final String deviceIdentification;

    public ClientConnectionEventListener(final String deviceIdentification, final ClientConnectionCache connectionCache,
            final ClientAsduHandlerRegistry asduHandlerRegistry, final ResponseMetadata responseMetadata) {
        this.connectionCache = connectionCache;
        this.asduHandlerRegistry = asduHandlerRegistry;
        this.responseMetadata = responseMetadata;
        this.deviceIdentification = deviceIdentification;
    }

    @Override
    public void newASdu(final ASdu asdu) {
        LOGGER.info("Received incoming ASDU {} from device {}", asdu, this.deviceIdentification);
        try {
            final ClientAsduHandler asduHandler = this.asduHandlerRegistry.getHandler(asdu);
            asduHandler.handleAsdu(asdu, this.responseMetadata);

        } catch (final Iec60870AsduHandlerNotFoundException e) {
            LOGGER.error("Unknown request received, no handler available for ASDU: {}", asdu, e);
        } catch (final Exception e) {
            LOGGER.error("Exception occurred while handling an incoming ASDU from device {}.",
                    this.deviceIdentification, e);
        }
    }

    @Override
    public void connectionClosed(final IOException e) {
        LOGGER.info("Connection with device {} closed.", this.deviceIdentification);
        LOGGER.debug("Exception: ", e);

        this.connectionCache.removeConnection(this.deviceIdentification);
    }
}
