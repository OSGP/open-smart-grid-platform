/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.LmdDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing public lighting get light sensor status request messages
 */
@Component
public class PublicLightingGetLightSensorStatusRequestMessageProcessor extends LmdDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetLightSensorStatusRequestMessageProcessor.class);

    protected PublicLightingGetLightSensorStatusRequestMessageProcessor() {
        super(MessageType.GET_LIGHT_SENSOR_STATUS);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opensmartgridplatform.shared.infra.jms.MessageProcessor#
     * processMessage(javax .jms.ObjectMessage)
     */
    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting get status request message");

        MessageMetadata messageMetadata;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        // To ensure the response is sent to the public lighting or common
        // domain and can be parsed using the existing GetStatus functions, the
        // message type is changed from GET_LIGHT_SENSOR_STATUS to
        // GET_LIGHT_STATUS or to GET_STATUS, depending on domain.
        String messageType = messageMetadata.getMessageType();
        if (DomainTypeDto.PUBLIC_LIGHTING.name().equals(messageMetadata.getDomain())) {
            messageType = MessageType.GET_LIGHT_STATUS.name();
        } else if (DomainTypeDto.TARIFF_SWITCHING.name().equals(messageMetadata.getDomain())) {
            LOGGER.warn("Unexpected domain request received: {} for messageType: {}", messageMetadata.getDomain(),
                    messageType);
        } else {
            messageType = MessageType.GET_STATUS.name();
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder()
                .messageMetadata(messageMetadata)
                .messageType(messageType)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest deviceRequest = DeviceRequest.newBuilder()
                .messageMetaData(messageMetadata)
                .messageType(messageType)
                .build();

        this.deviceService.getStatus(deviceRequest, iec61850DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender responseMessageSender,
            final DomainInformation domainInformation, final String messageType, final int retryCount,
            final boolean isScheduled) {
        LOGGER.info("Override for handleDeviceResponse() by PublicLightingGetLightSensorStatusRequestMessageProcessor");
        this.handleGetStatusDeviceResponse(deviceResponse, responseMessageSender, domainInformation, messageType,
                retryCount, isScheduled);
    }
}
