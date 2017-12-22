/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.application.services.ConfigurationService;
import org.osgp.adapter.protocol.dlms.application.services.FirmwareService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageType;
import org.osgp.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component
public class UpdateFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFirmwareRequestMessageProcessor.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FirmwareService firmwareService;

    @Autowired
    private OsgpRequestMessageSender osgpRequestMessageSender;

    protected UpdateFirmwareRequestMessageProcessor() {
        super(DeviceRequestMessageType.UPDATE_FIRMWARE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing {} request message", this.deviceRequestMessageType);
        MessageMetadata messageMetadata = null;

        try {
            messageMetadata = MessageMetadata.fromMessage(message);

            LOGGER.info("{} called for device: {} for organisation: {}", messageMetadata.getMessageType(),
                    messageMetadata.getDeviceIdentification(), messageMetadata.getOrganisationIdentification());

            final String firmwareIdentification = (String) message.getObject();

            if (this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)) {
                LOGGER.info("[{}] - Firmware file [{}] available. Updating firmware on device [{}]",
                        messageMetadata.getCorrelationUid(), firmwareIdentification,
                        messageMetadata.getDeviceIdentification());
                this.processUpdateFirmwareRequest(messageMetadata, firmwareIdentification);
            } else {
                LOGGER.info("[{}] - Firmware file [{}] not available. Sending GetFirmwareFile request to core.",
                        messageMetadata.getCorrelationUid(), firmwareIdentification);
                this.sendGetFirmwareFileRequest(messageMetadata, firmwareIdentification);
            }
        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        }

    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException, SessionProviderException {
        this.assertRequestObjectType(String.class, requestObject);

        final String firmwareIdentification = (String) requestObject;
        return this.configurationService.updateFirmware(conn, device, firmwareIdentification);
    }

    private void processUpdateFirmwareRequest(final MessageMetadata messageMetadata,
            final String firmwareIdentification) {

        DlmsConnectionHolder conn = null;
        DlmsDevice device = null;

        try {
            Serializable response = null;

            device = this.domainHelperService.findDlmsDevice(messageMetadata);
            conn = this.createConnectionForDevice(device, messageMetadata);

            response = this.handleMessage(conn, device, firmwareIdentification);

            // Send response
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, this.responseMessageSender,
                    response);
        } catch (final Exception exception) {
            // Return original request + exception
            LOGGER.error("Unexpected exception during {}", this.deviceRequestMessageType.name(), exception);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, exception,
                    this.responseMessageSender, firmwareIdentification);
        } finally {
            this.doConnectionPostProcessing(device, conn);
        }
    }

    private void sendGetFirmwareFileRequest(final MessageMetadata messageMetadata,
            final String firmwareIdentification) {
        final RequestMessage message = this.createRequestMessage(messageMetadata, firmwareIdentification);
        this.osgpRequestMessageSender.send(message, OsgpRequestMessageType.GET_FIRMWARE_FILE.name(), messageMetadata);
    }

    private RequestMessage createRequestMessage(final MessageMetadata messageMetadata, final Serializable messageData) {

        return new RequestMessage(messageMetadata.getCorrelationUid(), messageMetadata.getOrganisationIdentification(),
                messageMetadata.getDeviceIdentification(), messageData);

    }
}
