/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec60870.services.DeviceResponseService;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.support.JmsUtils;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class BaseMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageProcessor.class);

    @Autowired
    private int maxRedeliveriesForIec60870Requests;

    @Autowired
    private DeviceResponseMessageSender responseMessageSender;

    @Autowired
    private DeviceResponseService deviceResponseService;

    @Autowired
    @Qualifier("iec60870RequestMessageProcessorMap")
    private MessageProcessorMap iec60870RequestMessageProcessorMap;

    private MessageType messageType;

    /**
     * Each MessageProcessor should register its MessageType at construction.
     *
     * @param messageType
     *            The MessageType the MessageProcessor implementation can
     *            process.
     */
    protected BaseMessageProcessor(final MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the Map of MessageProcessors.
     */
    @PostConstruct
    public void init() {
        this.iec60870RequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
    }

    protected void printDomainInfo(final RequestMessageData requestMessageData) {
        LOGGER.info("Calling DeviceService function: {} for domain: {} {}", requestMessageData.getMessageType(),
                requestMessageData.getDomain(), requestMessageData.getDomainVersion());
    }

    /**
     * Handles {@link EmptyDeviceResponse} by default. MessageProcessor
     * implementations can override this function to handle responses containing
     * data.
     */
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException ex = null;

        try {
            final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
        } catch (final OsgpException e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            ex = e;
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, deviceResponse.getMessagePriority());
        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder()
                .domain(domainInformation.getDomain()).domainVersion(domainInformation.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(result).osgpException(ex).retryCount(retryCount)
                .build();
        responseMessageSender.send(protocolResponseMessage);
    }

    // TODO: retries werkend maken
    // TODO: nakijken of de "expected error" direct een response moet sturen of
    // dat die retries moet gebruiken.
    protected void handleExpectedErrorRuud(final MessageMetadata messageMetadata, final ProtocolAdapterException e)
            throws JMSException {
        LOGGER.error("Expected error while processing message - Ruud", e);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(messageMetadata);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder()
                .domain(messageMetadata.getDomain()).domainVersion(messageMetadata.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(ResponseMessageResultType.NOT_OK).osgpException(e)
                .retryCount(Integer.MAX_VALUE).build();

        if (this.hasRemainingRedeliveries(messageMetadata)) {
            this.redeliverMessage(messageMetadata, e);
        } else {
            this.sendErrorResponse(messageMetadata, protocolResponseMessage);
        }

    }

    private boolean hasRemainingRedeliveries(final MessageMetadata messageMetadata) {
        final int jmsxRedeliveryCount = messageMetadata.getJmsxDeliveryCount() - 1;
        LOGGER.info("jmsxDeliveryCount: {}, jmsxRedeliveryCount: {}, maxRedeliveriesForIec60870Requests: {}",
                messageMetadata.getJmsxDeliveryCount(), jmsxRedeliveryCount, this.maxRedeliveriesForIec60870Requests);

        return jmsxRedeliveryCount < this.maxRedeliveriesForIec60870Requests;
    }

    private void redeliverMessage(final MessageMetadata messageMetadata, final ProtocolAdapterException e) {
        final int jmsxRedeliveryCount = messageMetadata.getJmsxDeliveryCount() - 1;

        LOGGER.info(
                "Redelivering message with messageType: {}, correlationUid: {}, for device: {} - jmsxRedeliveryCount: {} is less than maxRedeliveriesForIec60870Requests: {}",
                messageMetadata.getMessageType(), messageMetadata.getCorrelationUid(),
                messageMetadata.getDeviceIdentification(), jmsxRedeliveryCount,
                this.maxRedeliveriesForIec60870Requests);
        final JMSException jmsException = new JMSException(
                e == null ? "checkForRedelivery() unknown error: OsgpException e is null" : e.getMessage());
        throw JmsUtils.convertJmsAccessException(jmsException);

    }

    private void sendErrorResponse(final MessageMetadata messageMetadata,
            final ProtocolResponseMessage protocolResponseMessage) {
        LOGGER.warn(
                "All redelivery attempts failed for message with messageType: {}, correlationUid: {}, for device: {}",
                messageMetadata.getMessageType(), messageMetadata.getCorrelationUid(),
                messageMetadata.getDeviceIdentification());

        this.responseMessageSender.send(protocolResponseMessage);
    }

}
