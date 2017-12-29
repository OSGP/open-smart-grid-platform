/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors;

import java.io.IOException;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.device.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OslpEnvelopeProcessor;
import com.alliander.osgp.dto.valueobjects.HistoryTermTypeDto;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.TimePeriodDto;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.SignedOslpEnvelopeDto;
import com.alliander.osgp.oslp.UnsignedOslpEnvelopeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

/**
 * Class for processing public lighting get power usage history request messages
 */
@Component("oslpPublicLightingGetPowerUsageHistoryRequestMessageProcessor")
public class PublicLightingGetPowerUsageHistoryRequestMessageProcessor extends DeviceRequestMessageProcessor implements
OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetPowerUsageHistoryRequestMessageProcessor.class);

    public PublicLightingGetPowerUsageHistoryRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting get power usage history request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        Boolean isScheduled = null;
        int retryCount = 0;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            isScheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            LOGGER.debug("scheduled: {}", isScheduled);
            return;
        }

        try {
            final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryMessageDataContainerDto = (PowerUsageHistoryMessageDataContainerDto) message
                    .getObject();

            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final GetPowerUsageHistoryDeviceRequest deviceRequest = new GetPowerUsageHistoryDeviceRequest(
                    organisationIdentification, deviceIdentification, correlationUid,
                    powerUsageHistoryMessageDataContainerDto, domain, domainVersion, messageType, ipAddress,
                    retryCount, isScheduled);

            this.deviceService.getPowerUsageHistory(deviceRequest);
        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain,
                    domainVersion, messageType, retryCount);
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
        final String ipAddress = unsignedOslpEnvelopeDto.getIpAddress();
        final int retryCount = unsignedOslpEnvelopeDto.getRetryCount();
        final boolean isScheduled = unsignedOslpEnvelopeDto.isScheduled();

        final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this
                        .handleGetPowerUsageHistoryDeviceResponse(deviceResponse, null,
                                PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.responseMessageSender,
                                domain, domainVersion, messageType, isScheduled, retryCount);
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                        deviceResponse, t, ((PowerUsageHistoryResponseMessageDataContainerDto) unsignedOslpEnvelopeDto
                                .getExtraData()).getRequestContainer(),
                        PublicLightingGetPowerUsageHistoryRequestMessageProcessor.this.responseMessageSender,
                        deviceResponse, domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        try {
            final PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer = (PowerUsageHistoryResponseMessageDataContainerDto) unsignedOslpEnvelopeDto
                    .getExtraData();
            final TimePeriodDto timePeriod = new TimePeriodDto(powerUsageHistoryResponseMessageDataContainer.getStartTime(),
                    powerUsageHistoryResponseMessageDataContainer.getEndTime());
            final HistoryTermTypeDto historyTermType = powerUsageHistoryResponseMessageDataContainer.getHistoryTermType();

            final GetPowerUsageHistoryDeviceRequest deviceRequest = new GetPowerUsageHistoryDeviceRequest(
                    organisationIdentification, deviceIdentification, correlationUid,
                    new PowerUsageHistoryMessageDataContainerDto(timePeriod, historyTermType), domain, domainVersion,
                    messageType, ipAddress, retryCount, isScheduled);

            this.deviceService.doGetPowerUsageHistory(oslpEnvelope, powerUsageHistoryResponseMessageDataContainer,
                    deviceRequest, deviceResponseHandler, ipAddress, domain, domainVersion, messageType, retryCount,
                    isScheduled);
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain,
                    domainVersion, messageType, retryCount);
        }
    }

    protected void handleGetPowerUsageHistoryDeviceResponse(final DeviceResponse deviceResponse,
            final PowerUsageHistoryMessageDataContainerDto messageData,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final boolean isScheduled, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainerDto = null;
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
                deviceResponse.getCorrelationUid(), messageType, MessagePriorityEnum.DEFAULT.getPriority());
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(osgpException).dataObject(dataObject).scheduled(isScheduled).retryCount(retryCount)
                .build();

        responseMessageSender.send(responseMessage);
    }
}
