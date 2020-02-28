/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.GetPowerUsageHistoryDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetPowerUsageHistoryDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing public lighting get power usage history request messages
 */
@Component("iec61850PublicLightingGetPowerUsageHistoryRequestMessageProcessor")
public class PublicLightingGetPowerUsageHistoryRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetPowerUsageHistoryRequestMessageProcessor.class);

    public PublicLightingGetPowerUsageHistoryRequestMessageProcessor() {
        super(MessageType.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting get power usage history request message");

        MessageMetadata messageMetadata;
        PowerUsageHistoryMessageDataContainerDto powerUsageHistoryMessageDataContainerDto;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            powerUsageHistoryMessageDataContainerDto = (PowerUsageHistoryMessageDataContainerDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder()
                .messageMetadata(messageMetadata)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest.Builder deviceRequestBuilder = DeviceRequest.newBuilder().messageMetaData(messageMetadata);

        this.deviceService.getPowerUsageHistory(
                new GetPowerUsageHistoryDeviceRequest(deviceRequestBuilder, powerUsageHistoryMessageDataContainerDto),
                iec61850DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount, final boolean isScheduled) {
        LOGGER.info("Override for handleDeviceResponse() by PublicLightingGetPowerUsageHistoryRequestMessageProcessor");
        this.handleGetPowerUsageHistoryDeviceResponse(deviceResponse, responseMessageSender, domainInformation,
                messageType, retryCount, isScheduled);
    }

    private void handleGetPowerUsageHistoryDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount, final boolean isScheduled) {

        final GetPowerUsageHistoryDeviceResponse getPowerUsageHistoryDeviceResponse = (GetPowerUsageHistoryDeviceResponse) deviceResponse;
        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer = null;

        try {
            powerUsageHistoryResponseMessageDataContainer = new PowerUsageHistoryResponseMessageDataContainerDto(
                    getPowerUsageHistoryDeviceResponse.getPowerUsageHistoryData());
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage.Builder builder = new ProtocolResponseMessage.Builder();
        final ProtocolResponseMessage responseMessage = builder.domain(domainInformation.getDomain())
                .domainVersion(domainInformation.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata)
                .result(result)
                .osgpException(osgpException)
                .dataObject(powerUsageHistoryResponseMessageDataContainer)
                .retryCount(retryCount)
                .retryHeader(new RetryHeader())
                .scheduled(isScheduled)
                .build();

        responseMessageSender.send(responseMessage);
    }
}
