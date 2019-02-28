/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import java.io.IOException;
import java.io.Serializable;

import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for creating and sending response messages.
 */
public abstract class BaseResponseEventListener implements ConnectionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseResponseEventListener.class);

    private final MessageMetadata messageMetadata;
    private final ResponseMessageSender responseMessageSender;
    private final DeviceMessageLoggingService deviceMessageLoggingService;

    public BaseResponseEventListener(final MessageMetadata messageMetadata,
            final ResponseMessageSender responseMessageSender,
            final DeviceMessageLoggingService deviceMessageLoggingService) {
        this.messageMetadata = messageMetadata;
        this.responseMessageSender = responseMessageSender;
        this.deviceMessageLoggingService = deviceMessageLoggingService;
    }

    @Override
    public void connectionClosed(final IOException e) {
        LOGGER.info("Connection to the device was closed", e);
    }

    /**
     * Sends the processed response from a device to OSGP core.
     *
     * @param response
     *            The response to send.
     */
    protected void sendResponse(final Serializable response) {

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(this.messageMetadata);
        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder()
                .domain(this.messageMetadata.getDomain()).domainVersion(this.messageMetadata.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(ResponseMessageResultType.OK)
                .retryCount(this.messageMetadata.getRetryCount()).dataObject(response).build();

        this.responseMessageSender.send(protocolResponseMessage);
    }

    public MessageMetadata getMessageMetadata() {
        return this.messageMetadata;
    }

    public DeviceMessageLoggingService getDeviceMessageLoggingService() {
        return this.deviceMessageLoggingService;
    }

}
