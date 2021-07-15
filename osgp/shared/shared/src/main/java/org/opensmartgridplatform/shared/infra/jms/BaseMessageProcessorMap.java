/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import java.util.EnumMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseMessageProcessorMap implements MessageProcessorMap {

  /** Logger for this class */
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageProcessorMap.class);

  protected Map<MessageType, MessageProcessor> messageProcessors = new EnumMap<>(MessageType.class);

  protected final String messageProcessorMapName;

  public BaseMessageProcessorMap(final String messageProcessorMapName) {
    this.messageProcessorMapName = messageProcessorMapName;
  }

  @Override
  public void setMessageProcessors(final Map<MessageType, MessageProcessor> messageProcessors) {
    LOGGER.info(
        "Setting MessageProcessors in {} for MessageTypes: {}",
        this.messageProcessorMapName,
        messageProcessors.keySet());
    this.messageProcessors.clear();
    this.messageProcessors.putAll(messageProcessors);
  }

  @Override
  public void addMessageProcessor(
      final MessageType messageType, final MessageProcessor messageProcessor) {
    LOGGER.info(
        "Putting MessageProcessor in {} for MessageType: {}",
        this.messageProcessorMapName,
        messageType);
    this.messageProcessors.put(messageType, messageProcessor);
  }

  @Override
  public MessageProcessor getMessageProcessor(final ObjectMessage message) throws JMSException {
    return this.getMessageProcessor(this.getJmsType(message));
  }

  private MessageProcessor getMessageProcessor(final String jmsType) throws JMSException {
    final MessageProcessor messageProcessor =
        this.messageProcessors.get(this.getMessageType(jmsType));
    if (messageProcessor == null) {
      LOGGER.error(
          "No message processor found in {} for message type: {}",
          this.messageProcessorMapName,
          jmsType);
      throw new JMSException("Message processor not configured");
    }
    return messageProcessor;
  }

  private String getJmsType(final ObjectMessage message) throws JMSException {
    final String jmsType = message.getJMSType();
    if (jmsType == null) {
      LOGGER.error(
          "Message type not set for message with JMS correlation ID: {} in {}",
          message.getJMSCorrelationID(),
          this.messageProcessorMapName);
      throw new JMSException("Message type not set");
    }
    return jmsType;
  }

  private MessageType getMessageType(final String jmsType) throws JMSException {
    try {
      return MessageType.valueOf(jmsType);
    } catch (final IllegalArgumentException e) {
      LOGGER.debug("Unknown message type", e);
      LOGGER.error("Unknown message type: {} in {}", jmsType, this.messageProcessorMapName);
      throw new JMSException("Unknown message type");
    }
  }
}
