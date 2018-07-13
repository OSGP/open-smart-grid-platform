/**
 * Copyright 2016 Smart Society Services B.V.
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
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.RtuDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataResponseDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

/**
 * Class for processing microgrids get data request messages
 */
@Component("iec61850MicrogridsGetDataRequestMessageProcessor")
public class MicrogridsGetDataRequestMessageProcessor extends RtuDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsGetDataRequestMessageProcessor.class);

    public MicrogridsGetDataRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_DATA);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing microgrids get data request message");

        MessageMetadata messageMetadata = null;
        GetDataRequestDto getDataRequest = null;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            getDataRequest = (GetDataRequestDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder().messageMetadata(messageMetadata)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest.Builder deviceRequestBuilder = DeviceRequest.newBuilder().messageMetaData(messageMetadata);

        this.deviceService.getData(new GetDataDeviceRequest(deviceRequestBuilder, getDataRequest),
                iec61850DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount) {
        LOGGER.info("Override for handleDeviceResponse() by MicrogridsGetDataRequestMessageProcessor");
        this.handleGetDataDeviceResponse(deviceResponse, responseMessageSender, domainInformation, messageType,
                retryCount);
    }

    private void handleGetDataDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        GetDataResponseDto dataResponse = null;

        try {
            final GetDataDeviceResponse response = (GetDataDeviceResponse) deviceResponse;
            dataResponse = response.getDataResponse();
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        final DeviceMessageMetadata deviceMessageMetaData = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage.Builder()
                .domain(domainInformation.getDomain()).domainVersion(domainInformation.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetaData).result(result).osgpException(osgpException)
                .dataObject(dataResponse).retryCount(retryCount).build();

        responseMessageSender.send(responseMessage);

    }
}
