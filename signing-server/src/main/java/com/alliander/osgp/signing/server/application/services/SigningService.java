/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.signing.server.application.services;

import java.security.PrivateKey;

import javax.annotation.Resource;
import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.SignedOslpEnvelopeDto;
import com.alliander.osgp.oslp.UnsignedOslpEnvelopeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.signing.server.infra.messaging.SigningServerResponseMessageSender;

@Service
@Qualifier("SigningServerSigningService")
public class SigningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigningService.class);

    @Autowired
    @Qualifier("signingServerPrivateKey")
    private PrivateKey privateKey;

    @Resource
    @Qualifier("signingServerSignatureProvider")
    private String signatureProvider;

    @Resource
    @Qualifier("signingServerSignature")
    private String signature;

    @Autowired
    private SigningServerResponseMessageSender signingServerResponseMessageSender;

    public void sign(final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto, final String correlationUid,
            final String deviceIdentification, final Destination replyToQueue) {

        // Check the basics.
        if (unsignedOslpEnvelopeDto == null) {
            LOGGER.error("UnsignedOslpEnvelopeDto instance is null, unable to sign message");
            return;
        }
        if (replyToQueue == null) {
            LOGGER.error("Destination replyToQueue is null, unable to send response to protocol-adapter");
            return;
        }

        LOGGER.info("Received message to sign for device: {} with correlationId: {}", deviceIdentification,
                correlationUid);

        // Sign the message.
        this.doSignMessage(unsignedOslpEnvelopeDto, correlationUid, deviceIdentification, replyToQueue);
    }

    private void doSignMessage(final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto, final String correlationUid,
            final String deviceIdentification, final Destination replyToQueue) {

        final byte[] deviceId = unsignedOslpEnvelopeDto.getDeviceId();
        final byte[] sequenceNumber = unsignedOslpEnvelopeDto.getSequenceNumber();
        final Message payloadMessage = unsignedOslpEnvelopeDto.getPayloadMessage();
        final String organisationIdentification = unsignedOslpEnvelopeDto.getOrganisationIdentification();

        final OslpEnvelope oslpEnvelope = new OslpEnvelope.Builder().withDeviceId(deviceId)
                .withSequenceNumber(sequenceNumber).withPrimaryKey(this.privateKey).withSignature(this.signature)
                .withProvider(this.signatureProvider).withPayloadMessage(payloadMessage).build();

        ResponseMessage responseMessage = null;

        if (oslpEnvelope == null) {
            LOGGER.error("Message for device: {} with correlationId: {} NOT SIGNED, sending error to protocol-adpater",
                    deviceIdentification, correlationUid);

            responseMessage = ResponseMessage.newResponseMessageBuilder().withCorrelationUid(correlationUid)
                    .withOrganisationIdentification(organisationIdentification)
                    .withDeviceIdentification(deviceIdentification).withResult(ResponseMessageResultType.NOT_OK)
                    .withOsgpException(
                            new OsgpException(ComponentType.UNKNOWN, "Failed to build signed OslpEnvelope", null))
                    .withDataObject(unsignedOslpEnvelopeDto).build();

        } else {
            LOGGER.info("Message for device: {} with correlationId: {} signed, sending response to protocol-adapter",
                    deviceIdentification, correlationUid);

            final SignedOslpEnvelopeDto signedOslpEnvelopeDto = new SignedOslpEnvelopeDto(oslpEnvelope,
                    unsignedOslpEnvelopeDto);

            responseMessage = ResponseMessage.newResponseMessageBuilder().withCorrelationUid(correlationUid)
                    .withOrganisationIdentification(organisationIdentification)
                    .withDeviceIdentification(deviceIdentification).withResult(ResponseMessageResultType.OK)
                    .withDataObject(signedOslpEnvelopeDto).build();
        }

        this.signingServerResponseMessageSender.send(responseMessage, "SIGNING_RESPONSE", replyToQueue);
    }
}
