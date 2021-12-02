/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
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
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class AbstractMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageProcessor.class);

  @Autowired private int maxRedeliveriesForIec60870Requests;

  @Autowired
  @Qualifier("protocolIec60870OutboundOsgpCoreResponsesMessageSender")
  private ResponseMessageSender responseMessageSender;

  @Autowired
  @Qualifier("protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap")
  private MessageProcessorMap messageProcessorMap;

  @Autowired private ClientConnectionService iec60870DeviceConnectionService;

  @Autowired private LoggingService iec60870LoggingService;

  @Autowired private PendingRequestsQueue pendingRequestsQueue;

  private final MessageType messageType;

  /**
   * Each MessageProcessor should register its MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected AbstractMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  protected ResponseMessageSender getResponseMessageSender() {
    return this.responseMessageSender;
  }

  protected LoggingService getLoggingService() {
    return this.iec60870LoggingService;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the Map of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.messageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.info("Processing message.");

    MessageMetadata messageMetadata = null;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      final RequestMetadata requestMetadata =
          RequestMetadata.newBuilder().messageMetadata(messageMetadata).build();
      final ClientConnection deviceConnection =
          this.iec60870DeviceConnectionService.getConnection(requestMetadata);
      this.process(deviceConnection, requestMetadata);
    } catch (final ProtocolAdapterException e) {
      this.handleError(messageMetadata, e);
    } catch (final Exception e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
    }
  }

  public abstract void process(
      final ClientConnection deviceConnection, RequestMetadata requestMetadata)
      throws ProtocolAdapterException;

  protected void printDomainInfo(final RequestMetadata requestMessageData) {
    LOGGER.info(
        "Calling DeviceService function: {} for domain: {}",
        requestMessageData.getMessageType(),
        requestMessageData.getDomainInfo());
  }

  protected void handleError(
      final MessageMetadata messageMetadata, final ProtocolAdapterException e) {
    LOGGER.warn("Error while processing message", e);

    this.pendingRequestsQueue.remove(
        messageMetadata.getDeviceIdentification(), messageMetadata.getCorrelationUid());

    final ProtocolResponseMessage protocolResponseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(ResponseMessageResultType.NOT_OK)
            .osgpException(e)
            .build();

    if (this.hasRemainingRedeliveries(messageMetadata)) {
      this.redeliverMessage(messageMetadata, e);
    } else {
      this.sendErrorResponse(messageMetadata, protocolResponseMessage);
    }
  }

  private boolean hasRemainingRedeliveries(final MessageMetadata messageMetadata) {
    final int jmsxRedeliveryCount = messageMetadata.getJmsxDeliveryCount() - 1;
    LOGGER.info(
        "jmsxDeliveryCount: {}, jmsxRedeliveryCount: {}, maxRedeliveriesForIec60870Requests: {}",
        messageMetadata.getJmsxDeliveryCount(),
        jmsxRedeliveryCount,
        this.maxRedeliveriesForIec60870Requests);

    return jmsxRedeliveryCount < this.maxRedeliveriesForIec60870Requests;
  }

  private void redeliverMessage(
      final MessageMetadata messageMetadata, final ProtocolAdapterException e) {
    final int jmsxRedeliveryCount = messageMetadata.getJmsxDeliveryCount() - 1;

    LOGGER.info(
        "Redelivering message with messageType: {}, correlationUid: {}, for device: {} - jmsxRedeliveryCount: {} is less than maxRedeliveriesForIec60870Requests: {}",
        messageMetadata.getMessageType(),
        messageMetadata.getCorrelationUid(),
        messageMetadata.getDeviceIdentification(),
        jmsxRedeliveryCount,
        this.maxRedeliveriesForIec60870Requests);
    final JMSException jmsException =
        new JMSException(
            e == null
                ? "redeliverMessage() unknown error: ProtocolAdapterException e is null"
                : e.getMessage());
    throw JmsUtils.convertJmsAccessException(jmsException);
  }

  private void sendErrorResponse(
      final MessageMetadata messageMetadata,
      final ProtocolResponseMessage protocolResponseMessage) {
    LOGGER.warn(
        "All redelivery attempts failed for message with messageType: {}, correlationUid: {}, for device: {}",
        messageMetadata.getMessageType(),
        messageMetadata.getCorrelationUid(),
        messageMetadata.getDeviceIdentification());

    this.responseMessageSender.send(protocolResponseMessage);
  }
}
