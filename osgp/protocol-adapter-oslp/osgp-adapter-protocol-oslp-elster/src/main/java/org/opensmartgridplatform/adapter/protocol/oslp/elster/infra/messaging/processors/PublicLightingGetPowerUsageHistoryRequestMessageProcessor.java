/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.GetPowerUsageHistoryDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetPowerUsageHistoryDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.HistoryTermTypeDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.TimePeriodDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing public lighting get power usage history request messages
 */
@Component("oslpPublicLightingGetPowerUsageHistoryRequestMessageProcessor")
public class PublicLightingGetPowerUsageHistoryRequestMessageProcessor extends DeviceRequestMessageProcessor
        implements OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetPowerUsageHistoryRequestMessageProcessor.class);

    public PublicLightingGetPowerUsageHistoryRequestMessageProcessor() {
        super(MessageType.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
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

        try {
            this.printDomainInfo(messageMetadata.getMessageType(), messageMetadata.getDomain(),
                    messageMetadata.getDomainVersion());

            final GetPowerUsageHistoryDeviceRequest deviceRequest = new GetPowerUsageHistoryDeviceRequest(
                    DeviceRequest.newBuilder().messageMetaData(messageMetadata),
                    powerUsageHistoryMessageDataContainerDto);

            this.deviceService.getPowerUsageHistory(deviceRequest);
        } catch (final RuntimeException e) {
            this.handleError(e, messageMetadata);
        }
    }

    @Override
    public void processSignedOslpEnvelope(final String deviceIdentification,
            final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();
        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final String correlationUid = unsignedOslpEnvelopeDto.getCorrelationUid();
        final String organisationIdentification = unsignedOslpEnvelopeDto.getOrganisationIdentification();
        final String domain = unsignedOslpEnvelopeDto.getDomain();
        final String domainVersion = unsignedOslpEnvelopeDto.getDomainVersion();
        final String messageType = unsignedOslpEnvelopeDto.getMessageType();
        final int messagePriority = unsignedOslpEnvelopeDto.getMessagePriority();
        final String ipAddress = unsignedOslpEnvelopeDto.getIpAddress();
        final int retryCount = unsignedOslpEnvelopeDto.getRetryCount();
        final boolean isScheduled = unsignedOslpEnvelopeDto.isScheduled();

        final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.handleGetPowerUsageHistoryDeviceResponse(
                        deviceResponse, null,
                        PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.responseMessageSender, domain,
                        domainVersion, messageType, isScheduled, retryCount);
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                        deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        try {
            final PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer = (PowerUsageHistoryResponseMessageDataContainerDto) unsignedOslpEnvelopeDto
                    .getExtraData();
            final TimePeriodDto timePeriod = new TimePeriodDto(
                    powerUsageHistoryResponseMessageDataContainer.getStartTime(),
                    powerUsageHistoryResponseMessageDataContainer.getEndTime());
            final HistoryTermTypeDto historyTermType = powerUsageHistoryResponseMessageDataContainer
                    .getHistoryTermType();

            final DeviceRequest.Builder builder = DeviceRequest.newBuilder()
                    .organisationIdentification(organisationIdentification).deviceIdentification(deviceIdentification)
                    .correlationUid(correlationUid).domain(domain).domainVersion(domainVersion).messageType(messageType)
                    .messagePriority(messagePriority).ipAddress(ipAddress).retryCount(retryCount)
                    .isScheduled(isScheduled);
            final GetPowerUsageHistoryDeviceRequest deviceRequest = new GetPowerUsageHistoryDeviceRequest(builder,
                    new PowerUsageHistoryMessageDataContainerDto(timePeriod, historyTermType));

            this.deviceService.doGetPowerUsageHistory(oslpEnvelope, powerUsageHistoryResponseMessageDataContainer,
                    deviceRequest, deviceResponseHandler, ipAddress);
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, messagePriority, retryCount);
        }
    }

    protected void handleGetPowerUsageHistoryDeviceResponse(final DeviceResponse deviceResponse,
            final PowerUsageHistoryMessageDataContainerDto messageData,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final boolean isScheduled, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainerDto;
        Serializable dataObject;

        try {
            final GetPowerUsageHistoryDeviceResponse response = (GetPowerUsageHistoryDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
            powerUsageHistoryResponseMessageDataContainerDto = new PowerUsageHistoryResponseMessageDataContainerDto(
                    response.getPowerUsageHistoryData());
            dataObject = powerUsageHistoryResponseMessageDataContainerDto;
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            dataObject = messageData;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while getting device power usage history", e);
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(osgpException).dataObject(dataObject).scheduled(isScheduled).retryCount(retryCount)
                .build();

        responseMessageSender.send(responseMessage);
    }
}
