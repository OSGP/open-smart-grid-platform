/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.services.oslp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.SigningServerRequestMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonGetConfigurationRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonGetFirmwareRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonGetStatusRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonRebootRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonSetConfigurationRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonSetEventNotificationsRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.SignedOslpEnvelopeDto;
import com.alliander.osgp.oslp.UnsignedOslpEnvelopeDto;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service
public class OslpSigningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpSigningService.class);

    private static final String SIGNING_REQUEST_MESSAGE_TYPE = "SIGNING_REQUEST";

    @Autowired
    private SigningServerRequestMessageSender signingServerRequestMessageSender;

    @Autowired
    @Qualifier("oslpCommonGetFirmwareRequestMessageProcessor")
    private CommonGetFirmwareRequestMessageProcessor commonGetFirmwareRequestMessageProcessor;

    @Autowired
    @Qualifier("oslpCommonGetConfigurationRequestMessageProcessor")
    private CommonGetConfigurationRequestMessageProcessor commonGetConfigurationRequestMessageProcessor;

    @Autowired
    @Qualifier("oslpCommonSetConfigurationRequestMessageProcessor")
    private CommonSetConfigurationRequestMessageProcessor commonSetConfigurationRequestMessageProcessor;

    @Autowired
    @Qualifier("oslpCommonGetStatusRequestMessageProcessor")
    private CommonGetStatusRequestMessageProcessor commonGetStatusRequestMessageProcessor;

    @Autowired
    @Qualifier("oslpCommonRebootRequestMessageProcessor")
    private CommonRebootRequestMessageProcessor commonRebootRequestMessageProcessor;

    @Autowired
    @Qualifier("oslpCommonSetEventNotificationsRequestMessageProcessor")
    private CommonSetEventNotificationsRequestMessageProcessor commonSetEventNotificationsRequestMessageProcessor;

    @Autowired
    private OslpChannelHandlerServer oslpChannelHandlerServer;

    /**
     * Build OslpEnvelope for an OSLP request using the arguments supplied and
     * have the envelope signed by the signing server.
     */
    public void buildAndSignEnvelope(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final byte[] deviceId, final byte[] sequenceNumber, final String ipAddress,
            final String domain, final String domainVersion, final String messageType, final int retryCount,
            final boolean isScheduled, final Oslp.Message payloadMessage) {

        // Create DTO to transfer data using request message.
        final UnsignedOslpEnvelopeDto oslpEnvelopeDto = new UnsignedOslpEnvelopeDto(sequenceNumber, deviceId,
                payloadMessage, ipAddress, domain, domainVersion, messageType, retryCount, isScheduled,
                organisationIdentification, correlationUid);
        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, oslpEnvelopeDto);

        // Send request message to signing server.
        this.signingServerRequestMessageSender.send(requestMessage, SIGNING_REQUEST_MESSAGE_TYPE);
    }

    /**
     * Build OslpEnvelope for an OSLP response using the arguments supplied and
     * have the envelope signed by the signing server.
     */
    public void buildAndSignEnvelope(final byte[] deviceId, final byte[] sequenceNumber,
            final Oslp.Message payloadMessage, final Integer channelId) {

        final String correlationUid = channelId.toString();

        // Create DTO to transfer data using request message.
        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = new UnsignedOslpEnvelopeDto(sequenceNumber, deviceId,
                payloadMessage, correlationUid);
        final RequestMessage requestMessage = new RequestMessage(correlationUid, "organisationIdentification",
                "deviceIdentification", unsignedOslpEnvelopeDto);

        // Send request message to signing server.
        this.signingServerRequestMessageSender.send(requestMessage, SIGNING_REQUEST_MESSAGE_TYPE);
    }

    /**
     * Handle incoming signed OslpEnvelope from signing server.
     */
    public void handleSignedOslpEnvelope(final SignedOslpEnvelopeDto signedOslpEnvelopeDto,
            final String deviceIdentification) {
        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();

        // Check if it's a request or response message.
        if (unsignedOslpEnvelopeDto.getType().equals(UnsignedOslpEnvelopeDto.OSLP_RESPONSE_TYPE)) {
            // Handle OSLP response message.
            this.handleSignedOslpResponse(signedOslpEnvelopeDto);
        } else {
            // Hanle OSLP request message.
            this.handleSignedOslpRequest(signedOslpEnvelopeDto, deviceIdentification);
        }
    }

    private void handleSignedOslpRequest(final SignedOslpEnvelopeDto signedOslpEnvelopeDto,
            final String deviceIdentification) {

        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();

        // Handle OSLP request message.
        LOGGER.info("-----------------------------------------------------------------------------");
        LOGGER.info("oslpEnvelope.size: {}", oslpEnvelope.getSize());
        LOGGER.info("-----------------------------------------------------------------------------");
        LOGGER.info("unsignedOslpEnvelopeDto.getCorrelationUid() : {}", unsignedOslpEnvelopeDto.getCorrelationUid());
        LOGGER.info("unsignedOslpEnvelopeDto.getDeviceId() : {}", unsignedOslpEnvelopeDto.getDeviceId());
        LOGGER.info("unsignedOslpEnvelopeDto.getDomain() : {}", unsignedOslpEnvelopeDto.getDomain());
        LOGGER.info("unsignedOslpEnvelopeDto.getDomainVersion() : {}", unsignedOslpEnvelopeDto.getDomainVersion());
        LOGGER.info("unsignedOslpEnvelopeDto.getIpAddress() : {}", unsignedOslpEnvelopeDto.getIpAddress());
        LOGGER.info("unsignedOslpEnvelopeDto.getMessageType() : {}", unsignedOslpEnvelopeDto.getMessageType());
        LOGGER.info("unsignedOslpEnvelopeDto.getOrganisationIdentification() : {}",
                unsignedOslpEnvelopeDto.getOrganisationIdentification());
        LOGGER.info("unsignedOslpEnvelopeDto.getPayloadMessage() : {}", unsignedOslpEnvelopeDto.getPayloadMessage()
                .toString());
        LOGGER.info("unsignedOslpEnvelopeDto.getRetryCount() : {}", unsignedOslpEnvelopeDto.getRetryCount());
        LOGGER.info("unsignedOslpEnvelopeDto.getSequenceNumber() : {}", unsignedOslpEnvelopeDto.getSequenceNumber());
        LOGGER.info("unsignedOslpEnvelopeDto.isScheduled() : {}", unsignedOslpEnvelopeDto.isScheduled());
        LOGGER.info("-----------------------------------------------------------------------------");

        // Try to convert message type to DeviceRequestMessageType member.
        final DeviceRequestMessageType deviceRequestMessageType = DeviceRequestMessageType
                .valueOf(unsignedOslpEnvelopeDto.getMessageType());

        // Handle message for message type.
        // TODO: move to own (service class/message processor) and add the other
        // functions too.
        if (deviceRequestMessageType == null) {
            LOGGER.error("Unknown messageType: {}", unsignedOslpEnvelopeDto.getMessageType());
        } else if (deviceRequestMessageType.equals(DeviceRequestMessageType.GET_FIRMWARE_VERSION)) {
            this.commonGetFirmwareRequestMessageProcessor.processSignedOslpEnvelope(deviceIdentification,
                    signedOslpEnvelopeDto);
        } else if (deviceRequestMessageType.equals(DeviceRequestMessageType.GET_CONFIGURATION)) {
            this.commonGetConfigurationRequestMessageProcessor.processSignedOslpEnvelope(deviceIdentification,
                    signedOslpEnvelopeDto);
        } else if (deviceRequestMessageType.equals(DeviceRequestMessageType.SET_CONFIGURATION)) {
            this.commonSetConfigurationRequestMessageProcessor.processSignedOslpEnvelope(deviceIdentification,
                    signedOslpEnvelopeDto);
        } else if (deviceRequestMessageType.equals(DeviceRequestMessageType.GET_STATUS)) {
            this.commonGetStatusRequestMessageProcessor.processSignedOslpEnvelope(deviceIdentification,
                    signedOslpEnvelopeDto);
        } else if (deviceRequestMessageType.equals(DeviceRequestMessageType.SET_REBOOT)) {
            this.commonRebootRequestMessageProcessor.processSignedOslpEnvelope(deviceIdentification,
                    signedOslpEnvelopeDto);
        } else if (deviceRequestMessageType.equals(DeviceRequestMessageType.SET_EVENT_NOTIFICATIONS)) {
            this.commonSetEventNotificationsRequestMessageProcessor.processSignedOslpEnvelope(deviceIdentification,
                    signedOslpEnvelopeDto);
        } else {
            LOGGER.error("Unhandled messageType: {}", unsignedOslpEnvelopeDto.getMessageType());
        }
    }

    private void handleSignedOslpResponse(final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();

        // Handle OSLP response message.
        LOGGER.info("-----------------------------------------------------------------------------");
        LOGGER.info("oslpEnvelope.size: {}", oslpEnvelope.getSize());
        LOGGER.info("-----------------------------------------------------------------------------");
        LOGGER.info("unsignedOslpEnvelopeDto.getCorrelationUid() : {}", unsignedOslpEnvelopeDto.getCorrelationUid());
        LOGGER.info("unsignedOslpEnvelopeDto.getDeviceId() : {}", unsignedOslpEnvelopeDto.getDeviceId());
        LOGGER.info("unsignedOslpEnvelopeDto.getSequenceNumber() : {}", unsignedOslpEnvelopeDto.getSequenceNumber());
        LOGGER.info("unsignedOslpEnvelopeDto.getPayloadMessage() : {}", unsignedOslpEnvelopeDto.getPayloadMessage()
                .toString());

        // Send the signed OSLP envelope to the channel handler server.
        this.oslpChannelHandlerServer.processSignedOslpEnvelope(signedOslpEnvelopeDto);
    }
}
