/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.ResumeScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetLightDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.LightValueMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.ResumeScheduleMessageDataContainerDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for processing public lighting set light request messages
 */
@Component("oslpPublicLightingSetLightRequestMessageProcessor")
public class PublicLightingSetLightRequestMessageProcessor extends DeviceRequestMessageProcessor
        implements OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingSetLightRequestMessageProcessor.class);

    @Autowired
    private PublicLightingResumeScheduleRequestMessageProcessor publicLightingResumeScheduleRequestMessageProcessor;

    public PublicLightingSetLightRequestMessageProcessor() {
        super(MessageType.SET_LIGHT);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting set light request message");

        MessageMetadata messageMetadata;
        LightValueMessageDataContainerDto lightValueMessageDataContainer;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            lightValueMessageDataContainer = (LightValueMessageDataContainerDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        try {
            this.printDomainInfo(messageMetadata.getMessageType(), messageMetadata.getDomain(),
                    messageMetadata.getDomainVersion());

            final SetLightDeviceRequest deviceRequest = new SetLightDeviceRequest(
                    DeviceRequest.newBuilder().messageMetaData(messageMetadata), lightValueMessageDataContainer);

            this.deviceService.setLight(deviceRequest);
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

        final DeviceResponseHandler setLightDeviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                if (((EmptyDeviceResponse) deviceResponse).getStatus().equals(DeviceMessageStatus.OK)) {
                    // If the response is OK, just log it. The resumeSchedule()
                    // function will be called next.
                    LOGGER.info("setLight() successful for device : {}", deviceResponse.getDeviceIdentification());
                } else {
                    // If the response is not OK, send a response message to the
                    // responses queue.
                    PublicLightingSetLightRequestMessageProcessor.this.handleEmptyDeviceResponse(deviceResponse,
                            PublicLightingSetLightRequestMessageProcessor.this.responseMessageSender, domain,
                            domainVersion, messageType, retryCount);
                }
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                PublicLightingSetLightRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(deviceResponse,
                        t, domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        final DeviceRequest setLightDeviceRequest = DeviceRequest.newBuilder()
                .organisationIdentification(organisationIdentification).deviceIdentification(deviceIdentification)
                .correlationUid(correlationUid).domain(domain).domainVersion(domainVersion).messageType(messageType)
                .messagePriority(messagePriority).ipAddress(ipAddress).retryCount(retryCount).isScheduled(isScheduled)
                .build();

        // Execute a ResumeSchedule call with 'immediate = false' and 'index
        // = 0' as arguments.
        final ResumeScheduleMessageDataContainerDto resumeScheduleMessageDataContainer = new ResumeScheduleMessageDataContainerDto(
                0, false);

        final DeviceResponseHandler resumeScheduleDeviceResponseHandler = this.publicLightingResumeScheduleRequestMessageProcessor
                .createResumeScheduleDeviceResponseHandler(domain, domainVersion, MessageType.RESUME_SCHEDULE.name(),
                        retryCount, isScheduled);

        final ResumeScheduleDeviceRequest resumeScheduleDeviceRequest = new ResumeScheduleDeviceRequest(DeviceRequest
                .newBuilder().organisationIdentification(organisationIdentification)
                .deviceIdentification(deviceIdentification).correlationUid(correlationUid).domain(domain)
                .domainVersion(domainVersion).messageType(MessageType.RESUME_SCHEDULE.name())
                .messagePriority(messagePriority).ipAddress(ipAddress).retryCount(retryCount).isScheduled(isScheduled),
                resumeScheduleMessageDataContainer);

        try {
            this.deviceService.doSetLight(oslpEnvelope, setLightDeviceRequest, resumeScheduleDeviceRequest,
                    setLightDeviceResponseHandler, resumeScheduleDeviceResponseHandler, ipAddress);
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, messagePriority, retryCount);
        }
    }
}
