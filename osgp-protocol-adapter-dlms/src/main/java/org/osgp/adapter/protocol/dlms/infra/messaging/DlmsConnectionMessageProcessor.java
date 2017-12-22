/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging;

import org.osgp.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;

/**
 * Abstract base class for message processors dealing with optional
 * DlmsConnection creation and DlmsMessageListener handling.
 */
public abstract class DlmsConnectionMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnectionMessageProcessor.class);

    @Autowired
    protected DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    protected DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

    @Autowired
    private SecurityKeyService securityKeyService;

    protected DlmsConnectionHolder createConnectionForDevice(final DlmsDevice device,
            final MessageMetadata messageMetadata) throws OsgpException {

        final InvocationCountingDlmsMessageListener dlmsMessageListener = this
                .createMessageListenerForDeviceConnection(device, messageMetadata);
        return this.dlmsConnectionFactory.getConnection(device, dlmsMessageListener);
    }

    protected InvocationCountingDlmsMessageListener createMessageListenerForDeviceConnection(final DlmsDevice device,
            final MessageMetadata messageMetadata) {
        final InvocationCountingDlmsMessageListener dlmsMessageListener;
        if (device.isInDebugMode()) {
            dlmsMessageListener = new LoggingDlmsMessageListener(device.getDeviceIdentification(),
                    this.dlmsLogItemRequestMessageSender);
            dlmsMessageListener.setMessageMetadata(messageMetadata);
            dlmsMessageListener.setDescription("Create connection");
        } else if (device.isHls5Active()) {
            dlmsMessageListener = new InvocationCountingDlmsMessageListener();
        } else {
            dlmsMessageListener = null;
        }
        return dlmsMessageListener;
    }

    protected void doConnectionPostProcessing(final DlmsDevice device, final DlmsConnectionHolder conn) {
        if (conn == null) {
            /*
             * No connection (possible and perfectly valid if an operation was
             * handled that did not involve device communication), then no
             * follow-up actions are required.
             */
            return;
        }

        this.closeDlmsConnection(device, conn);

        if (device.isHls5Active()) {
            this.updateInvocationCounterForEncryptionKey(device, conn);
        }
    }

    protected void closeDlmsConnection(final DlmsDevice device, final DlmsConnectionHolder conn) {
        LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
        final DlmsMessageListener dlmsMessageListener = conn.getDlmsMessageListener();
        dlmsMessageListener.setDescription("Close connection");
        try {
            conn.close();
        } catch (final Exception e) {
            LOGGER.error("Error while closing connection", e);
        }
    }

    protected void updateInvocationCounterForEncryptionKey(final DlmsDevice device, final DlmsConnectionHolder conn) {

        if (!(conn.getDlmsMessageListener() instanceof InvocationCountingDlmsMessageListener)) {
            LOGGER.error(
                    "updateInvocationCounterForEncryptionKey should only be called for devices with HLS 5 communication with an InvocationCountingDlmsMessageListener - device: {}, hls5: {}, listener: {}",
                    device.getDeviceIdentification(), device.isHls5Active(),
                    conn.getDlmsMessageListener() == null ? "null"
                            : conn.getDlmsMessageListener().getClass().getName());
            return;
        }

        final InvocationCountingDlmsMessageListener dlmsMessageListener = (InvocationCountingDlmsMessageListener) conn
                .getDlmsMessageListener();
        final int numberOfSentMessages = dlmsMessageListener.getNumberOfSentMessages();
        this.securityKeyService.incrementInvocationCounter(device.getDeviceIdentification(),
                SecurityKeyType.E_METER_ENCRYPTION, numberOfSentMessages);
    }
}
