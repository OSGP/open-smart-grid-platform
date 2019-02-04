/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.responses.GetHealthStatusDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services.Iec60870DeviceResponseHandler;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing get health status request messages
 */
@Component("iec60870GetHealthStatusRequestMessageProcessor")
public class GetHealthStatusRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

    public GetHealthStatusRequestMessageProcessor() {
        super(MessageType.GET_HEALTH_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.info("Processing get health status request message in new code...");

        MessageMetadata messageMetadata;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder().messageMetadata(messageMetadata)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec60870DeviceResponseHandler iec60870DeviceResponseHandler = this
                .createIec60870DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest deviceRequest = DeviceRequest.newBuilder().messageMetaData(messageMetadata).build();

        this.getDeviceService().getHealthStatus(deviceRequest, iec60870DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount) {
        LOGGER.info("Override for handleDeviceResponse() by GetHealthStatusRequestMessageProcessor");

        if (StringUtils.isEmpty(deviceResponse.getCorrelationUid())) {
            LOGGER.warn(
                    "CorrelationUID is null or empty, not sending GetHealthStatusResponse message for GetHealthStatusRequest message for device: {}",
                    deviceResponse.getDeviceIdentification());
            return;
        }

        final GetHealthStatusDeviceResponse response = (GetHealthStatusDeviceResponse) deviceResponse;
        final GetHealthStatusResponseDto status = response.getDeviceHealthStatus();

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, response.getMessagePriority());
        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder()
                .domain(domainInformation.getDomain()).domainVersion(domainInformation.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(ResponseMessageResultType.OK)
                .retryCount(retryCount).dataObject(status).build();
        responseMessageSender.send(protocolResponseMessage);
    }

}
