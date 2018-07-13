/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;

/**
 * Class for processing tariff switching get status request messages
 */
@Component("iec61850TariffSwitchingGetStatusRequestMessageProcessor")
public class TariffSwitchingGetStatusRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingGetStatusRequestMessageProcessor.class);

    public TariffSwitchingGetStatusRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_TARIFF_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing tariff switching get status request message");

        MessageMetadata messageMetadata = null;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder().messageMetadata(messageMetadata)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest deviceRequest = DeviceRequest.newBuilder().messageMetaData(messageMetadata).build();

        this.deviceService.getStatus(deviceRequest, iec61850DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final com.alliander.osgp.shared.infra.jms.ResponseMessageSender responseMessageSender,
            final DomainInformation domainInformation, final String messageType, final int retryCount) {
        LOGGER.info("Override for handleDeviceResponse() by TariffSwitchingGetStatusRequestMessageProcessor");
        this.handleGetStatusDeviceResponse(deviceResponse, responseMessageSender, domainInformation, messageType,
                retryCount);
    }
}
