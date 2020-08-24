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

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.GetConfigurationDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageTypeDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing public lighting set schedule request messages
 */
@Component("oslpPublicLightingSetScheduleRequestMessageProcessor")
public class PublicLightingSetScheduleRequestMessageProcessor extends DeviceRequestMessageProcessor
        implements OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingSetScheduleRequestMessageProcessor.class);

    private static final String LOG_MESSAGE_CALL_DEVICE_SERVICE = "Calling DeviceService function: {} of type {} for domain: {} {}";

    public PublicLightingSetScheduleRequestMessageProcessor() {
        super(MessageType.SET_LIGHT_SCHEDULE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting set schedule request message");

        MessageMetadata messageMetadata;
        ScheduleDto schedule;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            schedule = (ScheduleDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        try {
            ScheduleMessageDataContainerDto.Builder builder = new ScheduleMessageDataContainerDto.Builder(schedule);
            if (schedule.getAstronomicalSunriseOffset() != null || schedule.getAstronomicalSunsetOffset() != null) {
                builder = builder.withScheduleMessageType(ScheduleMessageTypeDto.RETRIEVE_CONFIGURATION);
            }
            final ScheduleMessageDataContainerDto scheduleMessageDataContainer = builder.build();

            this.printDomainInfo(messageMetadata.getMessageType(), messageMetadata.getDomain(),
                    messageMetadata.getDomainVersion());

            final SetScheduleDeviceRequest deviceRequest = new SetScheduleDeviceRequest(
                    DeviceRequest.newBuilder().messageMetaData(messageMetadata), scheduleMessageDataContainer,
                    RelayTypeDto.LIGHT);

            this.deviceService.setSchedule(deviceRequest);
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
        final ScheduleMessageDataContainerDto dataContainer = (ScheduleMessageDataContainerDto) unsignedOslpEnvelopeDto
                .getExtraData();

        final DeviceRequest.Builder builder = DeviceRequest.newBuilder()
                .organisationIdentification(organisationIdentification)
                .deviceIdentification(deviceIdentification)
                .correlationUid(correlationUid)
                .domain(domain)
                .domainVersion(domainVersion)
                .messageType(messageType)
                .messagePriority(messagePriority)
                .ipAddress(ipAddress)
                .retryCount(retryCount)
                .isScheduled(isScheduled);

        final SetScheduleDeviceRequest deviceRequest = new SetScheduleDeviceRequest(builder, dataContainer,
                RelayTypeDto.LIGHT);

        final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                PublicLightingSetScheduleRequestMessageProcessor.this.handleResponse(deviceResponse, deviceRequest);
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                PublicLightingSetScheduleRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                        deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        try {
            this.deviceService.doSetSchedule(oslpEnvelope, deviceRequest, deviceResponseHandler, ipAddress, domain,
                    domainVersion, messageType, messagePriority, retryCount, isScheduled, dataContainer.getPageInfo());
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, messagePriority, retryCount);
        }
    }

    private void handleResponse(final DeviceResponse deviceResponse, final SetScheduleDeviceRequest deviceRequest) {
        final ScheduleMessageTypeDto scheduleMessageTypeDto = deviceRequest.getScheduleMessageDataContainer()
                .getScheduleMessageType();

        switch (scheduleMessageTypeDto) {
        case RETRIEVE_CONFIGURATION:
            final GetConfigurationDeviceResponse response = (GetConfigurationDeviceResponse) deviceResponse;
            this.handleGetConfigurationBeforeSetScheduleResponse(deviceRequest, response);
            break;
        case SET_ASTRONOMICAL_OFFSETS:
            this.handleSetScheduleAstronomicalOffsetsResponse(deviceRequest);
            break;
        case SET_SCHEDULE:
        default:
            this.handleEmptyDeviceResponse(deviceResponse, this.responseMessageSender, deviceRequest.getDomain(),
                    deviceRequest.getDomainVersion(), deviceRequest.getMessageType(), deviceRequest.getRetryCount());
        }

    }

    private void handleGetConfigurationBeforeSetScheduleResponse(final SetScheduleDeviceRequest deviceRequest,
            final GetConfigurationDeviceResponse deviceResponse) {
        // Configuration is retrieved, so now continue with setting the
        // astronomical offsets
        LOGGER.info(LOG_MESSAGE_CALL_DEVICE_SERVICE, deviceRequest.getMessageType(),
                ScheduleMessageTypeDto.SET_ASTRONOMICAL_OFFSETS, deviceRequest.getDomain(),
                deviceRequest.getDomainVersion());

        final ScheduleMessageDataContainerDto dataContainer = new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleMessageDataContainer().getSchedule())
                        .withConfiguration(deviceResponse.getConfiguration())
                        .withScheduleMessageType(ScheduleMessageTypeDto.SET_ASTRONOMICAL_OFFSETS)
                        .build();

        final SetScheduleDeviceRequest newDeviceRequest = new SetScheduleDeviceRequest(
                createDeviceRequestBuilder(deviceRequest), dataContainer, RelayTypeDto.LIGHT);

        this.deviceService.setSchedule(newDeviceRequest);
    }

    private void handleSetScheduleAstronomicalOffsetsResponse(final SetScheduleDeviceRequest deviceRequest) {

        // Astronomical offsets are set, so now continue with the actual
        // schedule
        LOGGER.info(LOG_MESSAGE_CALL_DEVICE_SERVICE, deviceRequest.getMessageType(),
                ScheduleMessageTypeDto.SET_SCHEDULE, deviceRequest.getDomain(), deviceRequest.getDomainVersion());

        final ScheduleMessageDataContainerDto dataContainer = new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleMessageDataContainer().getSchedule())
                        .withScheduleMessageType(ScheduleMessageTypeDto.SET_SCHEDULE)
                        .build();

        final SetScheduleDeviceRequest newDeviceRequest = new SetScheduleDeviceRequest(
                createDeviceRequestBuilder(deviceRequest), dataContainer, RelayTypeDto.LIGHT);

        this.deviceService.setSchedule(newDeviceRequest);
    }

    private static DeviceRequest.Builder createDeviceRequestBuilder(final DeviceRequest deviceRequest) {
        return DeviceRequest.newBuilder()
                .organisationIdentification(deviceRequest.getOrganisationIdentification())
                .deviceIdentification(deviceRequest.getDeviceIdentification())
                .correlationUid(deviceRequest.getCorrelationUid())
                .domain(deviceRequest.getDomain())
                .domainVersion(deviceRequest.getDomainVersion())
                .messageType(deviceRequest.getMessageType())
                .messagePriority(deviceRequest.getMessagePriority())
                .ipAddress(deviceRequest.getIpAddress())
                .retryCount(deviceRequest.getRetryCount())
                .isScheduled(deviceRequest.isScheduled());
    }

}
