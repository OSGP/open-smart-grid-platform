/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.responses.from.core.processors;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.application.services.FirmwareService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.osgp.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.osgp.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.responses.from.core.OsgpResponseMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.DeviceFunctionDto;
import com.alliander.osgp.dto.valueobjects.FirmwareFileDto;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component
public class GetFirmwareFileResponseMessageProcessor extends OsgpResponseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareFileResponseMessageProcessor.class);

    @Autowired
    private FirmwareService firmwareService;

    protected GetFirmwareFileResponseMessageProcessor() {
        super(OsgpRequestMessageType.GET_FIRMWARE_FILE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing {} request message", this.osgpRequestMessageType.name());
        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        DlmsConnectionHolder conn = null;
        DlmsDevice device = null;

        final boolean isScheduled = this.getBooleanPropertyValue(message, Constants.IS_SCHEDULED);

        try {
            messageMetadata.handleMessage(message);

            device = this.domainHelperService.findDlmsDevice(messageMetadata);

            LOGGER.info("{} called for device: {} for organisation: {}", message.getJMSType(),
                    messageMetadata.getDeviceIdentification(), messageMetadata.getOrganisationIdentification());

            Serializable response = null;
            final LoggingDlmsMessageListener dlmsMessageListener;
            if (device.isInDebugMode()) {
                dlmsMessageListener = new LoggingDlmsMessageListener(device.getDeviceIdentification(),
                        this.dlmsLogItemRequestMessageSender);
                dlmsMessageListener.setMessageMetadata(messageMetadata);
                dlmsMessageListener.setDescription("Create connection");
            } else {
                dlmsMessageListener = null;
            }
            conn = this.dlmsConnectionFactory.getConnection(device, dlmsMessageListener);
            response = this.handleMessage(conn, device, message.getObject());

            messageMetadata.setMessageType(DeviceFunctionDto.UPDATE_FIRMWARE.name());
            // Send response
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, this.responseMessageSender,
                    response, isScheduled);

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        } catch (final Exception exception) {
            // Return original request + exception
            LOGGER.error("Unexpected exception during {}", this.osgpRequestMessageType.name(), exception);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, exception,
                    this.responseMessageSender, message.getObject(), isScheduled);
        } finally {
            if (conn != null) {
                LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
                conn.getDlmsMessageListener().setDescription("Close connection");
                try {
                    conn.close();
                } catch (final Exception e) {
                    LOGGER.error("Error while closing connection", e);
                }
            }
        }
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable response) throws ProtocolAdapterException {

        if (!(response instanceof ResponseMessage)) {
            throw new ProtocolAdapterException("Invalid response type, expected ResponseMessage object.");
        }

        final ResponseMessage responseMessage = (ResponseMessage) response;

        if (ResponseMessageResultType.OK.equals(responseMessage.getResult())) {
            final FirmwareFileDto firmwareFileDto = (FirmwareFileDto) responseMessage.getDataObject();
            return (Serializable) this.firmwareService.updateFirmware(conn, device, firmwareFileDto);
        } else {
            throw new ProtocolAdapterException("Get Firmware File failed.", responseMessage.getOsgpException());
        }

    }
}
