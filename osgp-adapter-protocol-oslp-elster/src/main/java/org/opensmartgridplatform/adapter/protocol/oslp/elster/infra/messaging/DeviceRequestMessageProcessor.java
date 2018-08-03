/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.DeviceService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.services.DeviceResponseService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DeviceRequestMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessor.class);

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceResponseMessageSender responseMessageSender;

    @Autowired
    protected DeviceResponseService deviceResponseService;

    @Autowired
    @Qualifier("protocolOslpDeviceRequestMessageProcessorMap")
    protected MessageProcessorMap oslpRequestMessageProcessorMap;

    protected final DeviceRequestMessageType deviceRequestMessageType;

    protected static final String UNEXPECTED_EXCEPTION = "An unknown error occurred";

    /**
     * Each MessageProcessor should register it's MessageType at construction.
     *
     * @param deviceRequestMessageType
     *            The MessageType the MessageProcessor implementation can
     *            process.
     */
    protected DeviceRequestMessageProcessor(final DeviceRequestMessageType deviceRequestMessageType) {
        this.deviceRequestMessageType = deviceRequestMessageType;
    }

    protected void printDomainInfo(final String messageType, final String domain, final String domainVersion) {
        LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.oslpRequestMessageProcessorMap.addMessageProcessor(this.deviceRequestMessageType.ordinal(),
                this.deviceRequestMessageType.name(), this);
    }

    protected void handleEmptyDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        TechnicalException ex = null;

        try {
            final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
        } catch (final TechnicalException e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            ex = e;
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(ex).retryCount(retryCount).build();

        responseMessageSender.send(responseMessage);
    }

    protected void handleScheduledEmptyDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final boolean isScheduled, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        TechnicalException ex = null;

        try {
            final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
        } catch (final TechnicalException e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            ex = e;
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(ex).scheduled(isScheduled).retryCount(retryCount).build();

        responseMessageSender.send(responseMessage);
    }

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion,
            final String messageType, final int messagePriority, final int retryCount) {
        LOGGER.error("Error while processing message", e);
        final TechnicalException ex = new TechnicalException(ComponentType.PROTOCOL_OSLP, UNEXPECTED_EXCEPTION, e);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, messageType, messagePriority);
        final ProtocolResponseMessage protocolResponseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata)
                .result(ResponseMessageResultType.NOT_OK).osgpException(ex).retryCount(retryCount).build();

        this.responseMessageSender.send(protocolResponseMessage);
    }

    protected void handleError(final Exception e, final MessageMetadata messageMetadata) {
        LOGGER.error("Error while processing message, using MessageMetadata to create error response", e);
        final TechnicalException ex = new TechnicalException(ComponentType.PROTOCOL_OSLP, UNEXPECTED_EXCEPTION, e);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(messageMetadata);
        final ProtocolResponseMessage protocolResponseMessage = ProtocolResponseMessage.newBuilder()
                .domain(messageMetadata.getDomain()).domainVersion(messageMetadata.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(ResponseMessageResultType.NOT_OK).osgpException(ex)
                .retryCount(messageMetadata.getRetryCount()).build();

        this.responseMessageSender.send(protocolResponseMessage);
    }

    protected void handleExpectedError(final Exception e, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String domain,
            final String domainVersion, final String messageType, final int messagePriority) {
        LOGGER.error("Expected error while processing message", e);
        final FunctionalException ex = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                ComponentType.PROTOCOL_OSLP, e);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, messageType, messagePriority);
        final ProtocolResponseMessage protocolResponseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata)
                .result(ResponseMessageResultType.NOT_OK).osgpException(ex).build();

        this.responseMessageSender.send(protocolResponseMessage);
    }

    public void handleUnableToConnectDeviceResponse(final DeviceResponse deviceResponse, final Throwable t,
            final Serializable messageData, final DeviceResponseMessageSender responseMessageSender,
            final DeviceResponse deviceResponse2, final String domain, final String domainVersion,
            final String messageType, final boolean isScheduled, final int retryCount) {

        final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
        final OsgpException ex = new TechnicalException(ComponentType.PROTOCOL_OSLP,
                StringUtils.isBlank(t.getMessage()) ? UNEXPECTED_EXCEPTION : t.getMessage(), t);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(ex).dataObject(messageData).scheduled(isScheduled).retryCount(retryCount).build();

        this.responseMessageSender.send(responseMessage);
    }
}
