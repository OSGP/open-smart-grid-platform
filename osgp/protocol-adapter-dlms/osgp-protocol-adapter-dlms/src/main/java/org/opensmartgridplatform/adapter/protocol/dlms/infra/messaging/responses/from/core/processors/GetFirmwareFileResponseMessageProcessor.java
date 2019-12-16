/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.processors;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.SilentException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.OsgpResponseMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFirmwareFileResponseMessageProcessor extends OsgpResponseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareFileResponseMessageProcessor.class);

    @Autowired
    private FirmwareService firmwareService;

    protected GetFirmwareFileResponseMessageProcessor() {
        super(MessageType.GET_FIRMWARE_FILE);
    }

    @SuppressWarnings("squid:S1193") // SilentException cannot be caught since it does not extend Exception.
    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing {} response message", this.messageType.name());
        MessageMetadata messageMetadata = null;

        DlmsConnectionManager conn = null;
        DlmsDevice device = null;

        try {
            // Get metadata from message and update message type to update
            // firmware
            messageMetadata = new MessageMetadata.Builder(MessageMetadata.fromMessage(message)).withMessageType(
                    MessageType.UPDATE_FIRMWARE.name()).build();

            device = this.domainHelperService.findDlmsDevice(messageMetadata);

            LOGGER.info("{} called for device: {} for organisation: {}", message.getJMSType(),
                    messageMetadata.getDeviceIdentification(), messageMetadata.getOrganisationIdentification());

            final Serializable response;
            conn = this.createConnectionForDevice(device, messageMetadata);
            response = this.handleMessage(conn, device, message.getObject());

            // Send response
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, this.responseMessageSender,
                    response);

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        } catch (final Exception exception) {
            // Return original request + exception
            if (!(exception instanceof SilentException)) {
                LOGGER.error("Unexpected exception during {}", this.messageType.name(), exception);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, exception,
                    this.responseMessageSender, message.getObject());
        } finally {
            this.doConnectionPostProcessing(device, conn);
        }
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionManager conn, final DlmsDevice device,
            final Serializable response) throws OsgpException {

        if (!(response instanceof ResponseMessage)) {
            throw new ProtocolAdapterException("Invalid response type, expected ResponseMessage object.");
        }

        final ResponseMessage responseMessage = (ResponseMessage) response;

        if (ResponseMessageResultType.OK.equals(responseMessage.getResult())) {
            final FirmwareFileDto firmwareFileDto = (FirmwareFileDto) responseMessage.getDataObject();
            return this.firmwareService.updateFirmware(conn, device, firmwareFileDto);
        } else {
            throw new ProtocolAdapterException("Get Firmware File failed.", responseMessage.getOsgpException());
        }

    }
}
