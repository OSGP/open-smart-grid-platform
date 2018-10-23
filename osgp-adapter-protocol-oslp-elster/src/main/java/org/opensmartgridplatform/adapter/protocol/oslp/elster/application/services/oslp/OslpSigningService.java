/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessorMap;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.SigningServerRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerServer;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OslpSigningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpSigningService.class);

    private static final String SIGNING_REQUEST_MESSAGE_TYPE = "SIGNING_REQUEST";

    private static final String LINES = "-----------------------------------------------------------------------------";

    @Autowired
    private SigningServerRequestMessageSender signingServerRequestMessageSender;

    @Autowired
    private DeviceResponseMessageSender deviceResponseMessageSender;

    private OslpChannelHandlerServer oslpChannelHandlerServer;

    @Autowired
    @Qualifier("protocolOslpDeviceRequestMessageProcessorMap")
    private DeviceRequestMessageProcessorMap deviceRequestMessageProcessorMap;

    /**
     * Build OslpEnvelope for an OSLP request using the arguments supplied and
     * have the envelope signed by the signing server.
     */
    public void buildAndSignEnvelope(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final byte[] deviceId, final byte[] sequenceNumber, final String ipAddress,
            final String domain, final String domainVersion, final String messageType, final int messagePriority,
            final int retryCount, final boolean isScheduled, final Oslp.Message payloadMessage,
            final Serializable extraData) {

        // Create DTO to transfer data using request message.
        final UnsignedOslpEnvelopeDto oslpEnvelopeDto = new UnsignedOslpEnvelopeDto(sequenceNumber, deviceId,
                payloadMessage, ipAddress, domain, domainVersion, messageType, messagePriority, retryCount, isScheduled,
                organisationIdentification, correlationUid, extraData);
        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, oslpEnvelopeDto);

        // Send request message to signing server.
        this.signingServerRequestMessageSender.send(requestMessage, SIGNING_REQUEST_MESSAGE_TYPE, messagePriority);
    }

    /**
     * Build OslpEnvelope for an OSLP response using the arguments supplied and
     * have the envelope signed by the signing server.
     */
    public void buildAndSignEnvelope(final byte[] deviceId, final byte[] sequenceNumber,
            final Oslp.Message payloadMessage, final Integer channelId,
            final OslpChannelHandlerServer oslpChannelHandlerServer) {

        this.oslpChannelHandlerServer = oslpChannelHandlerServer;
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
            // Handle OSLP request message.
            this.handleSignedOslpRequest(signedOslpEnvelopeDto, deviceIdentification);
        }
    }

    private void handleSignedOslpRequest(final SignedOslpEnvelopeDto signedOslpEnvelopeDto,
            final String deviceIdentification) {

        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();

        // Handle OSLP request message.
        LOGGER.debug(LINES);
        LOGGER.info("oslpEnvelope.size: {} for message type: {}", oslpEnvelope.getSize(),
                unsignedOslpEnvelopeDto.getMessageType());
        LOGGER.debug(LINES);
        LOGGER.debug("unsignedOslpEnvelopeDto.getCorrelationUid() : {}", unsignedOslpEnvelopeDto.getCorrelationUid());
        LOGGER.debug("unsignedOslpEnvelopeDto.getDeviceId() : {}", unsignedOslpEnvelopeDto.getDeviceId());
        LOGGER.debug("unsignedOslpEnvelopeDto.getDomain() : {}", unsignedOslpEnvelopeDto.getDomain());
        LOGGER.debug("unsignedOslpEnvelopeDto.getDomainVersion() : {}", unsignedOslpEnvelopeDto.getDomainVersion());
        LOGGER.debug("unsignedOslpEnvelopeDto.getIpAddress() : {}", unsignedOslpEnvelopeDto.getIpAddress());
        LOGGER.debug("unsignedOslpEnvelopeDto.getMessageType() : {}", unsignedOslpEnvelopeDto.getMessageType());
        LOGGER.debug("unsignedOslpEnvelopeDto.getMessagePriority() : {}", unsignedOslpEnvelopeDto.getMessagePriority());
        LOGGER.debug("unsignedOslpEnvelopeDto.getOrganisationIdentification() : {}",
                unsignedOslpEnvelopeDto.getOrganisationIdentification());
        LOGGER.debug("unsignedOslpEnvelopeDto.getPayloadMessage() : {}",
                unsignedOslpEnvelopeDto.getPayloadMessage().toString());
        LOGGER.debug("unsignedOslpEnvelopeDto.getRetryCount() : {}", unsignedOslpEnvelopeDto.getRetryCount());
        LOGGER.debug("unsignedOslpEnvelopeDto.getSequenceNumber() : {}", unsignedOslpEnvelopeDto.getSequenceNumber());
        LOGGER.debug("unsignedOslpEnvelopeDto.isScheduled() : {}", unsignedOslpEnvelopeDto.isScheduled());
        LOGGER.debug(LINES);

        final MessageType messageType = MessageType.valueOf(unsignedOslpEnvelopeDto.getMessageType());

        // Handle message for message type.
        final OslpEnvelopeProcessor messageProcessor = this.deviceRequestMessageProcessorMap
                .getOslpEnvelopeProcessor(messageType);
        if (messageProcessor == null) {
            LOGGER.error("No message processor for messageType: {}", unsignedOslpEnvelopeDto.getMessageType());
            return;
        }
        messageProcessor.processSignedOslpEnvelope(deviceIdentification, signedOslpEnvelopeDto);
    }

    private void handleSignedOslpResponse(final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();

        // Handle OSLP response message.
        LOGGER.debug(LINES);
        LOGGER.info("oslpEnvelope.size: {} for message type: {}", oslpEnvelope.getSize(),
                unsignedOslpEnvelopeDto.getMessageType());
        LOGGER.debug(LINES);
        LOGGER.debug("unsignedOslpEnvelopeDto.getCorrelationUid() : {}", unsignedOslpEnvelopeDto.getCorrelationUid());
        LOGGER.debug("unsignedOslpEnvelopeDto.getDeviceId() : {}", unsignedOslpEnvelopeDto.getDeviceId());
        LOGGER.debug("unsignedOslpEnvelopeDto.getSequenceNumber() : {}", unsignedOslpEnvelopeDto.getSequenceNumber());
        LOGGER.debug("unsignedOslpEnvelopeDto.getPayloadMessage() : {}",
                unsignedOslpEnvelopeDto.getPayloadMessage().toString());

        // Send the signed OSLP envelope to the channel handler server.
        this.oslpChannelHandlerServer.processSignedOslpEnvelope(signedOslpEnvelopeDto);
    }

    /**
     * Handle an error from the signing server.
     */
    public void handleError(final String deviceIdentification, final ResponseMessage responseMessage) {

        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = (UnsignedOslpEnvelopeDto) responseMessage
                .getDataObject();
        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                unsignedOslpEnvelopeDto.getOrganisationIdentification(), unsignedOslpEnvelopeDto.getCorrelationUid(),
                unsignedOslpEnvelopeDto.getMessageType(), responseMessage.getMessagePriority());
        final ProtocolResponseMessage protocolResponseMessage = ProtocolResponseMessage.newBuilder()
                .domain(unsignedOslpEnvelopeDto.getDomain()).domainVersion(unsignedOslpEnvelopeDto.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(responseMessage.getResult())
                .osgpException(responseMessage.getOsgpException()).scheduled(unsignedOslpEnvelopeDto.isScheduled())
                .build();
        this.deviceResponseMessageSender.send(protocolResponseMessage);
    }
}
