/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.infra.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "loggingMessageSender")
public class LoggingMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingMessageSender.class);

  @Value("${application.createJsonMessage:false}")
  private boolean createJsonMessage;

  @Autowired
  @Qualifier("loggingJmsTemplate")
  private JmsTemplate loggingJmsTemplate;

  /**
   * Method for sending a logging message to the queue.
   *
   * @param loggingMessage The LoggingRequestMessage request message to send.
   */
  public void send(final LoggingRequestMessage loggingMessage) {
    LOGGER.debug("Sending logger message");
    this.sendMessage(loggingMessage);
  }

  /**
   * Method for sending a logging message to the logger queue.
   *
   * @param loggingMessage The LoggingRequestMessage request message to send.
   */
  private void sendMessage(final LoggingRequestMessage loggingMessage) {
    LOGGER.info("Sending logger message to queue");

    this.loggingJmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            if (isCreateJsonMessage()) {
              return getJsonMessage(loggingMessage, session);
            }
            return getObjectMessage(session, loggingMessage);
          }
        });
  }

  public TextMessage getJsonMessage(
      final LoggingRequestMessage loggingMessage, final Session session) {
    TextMessage textMessage = null;
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonString = mapper.writeValueAsString(loggingMessage);
      textMessage = session.createTextMessage(jsonString);
    } catch (final Exception e) {
      LOGGER.error("Error creating json message : {}", e.getMessage());
    }
    return textMessage;
  }

  public ObjectMessage getObjectMessage(
      final Session session, final LoggingRequestMessage loggingMessage) throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();
    objectMessage.setJMSCorrelationID(loggingMessage.getCorrelationUid());
    objectMessage.setLongProperty(Constants.TIME_STAMP, loggingMessage.getTimeStamp().getTime());
    objectMessage.setStringProperty(Constants.CLASS_NAME, loggingMessage.getClassName());
    objectMessage.setStringProperty(Constants.METHOD_NAME, loggingMessage.getMethodName());
    objectMessage.setStringProperty(
        Constants.ORGANISATION_IDENTIFICATION, loggingMessage.getOrganisationIdentification());
    objectMessage.setStringProperty(Constants.USER_NAME, loggingMessage.getUserName());
    objectMessage.setStringProperty(
        Constants.APPLICATION_NAME, loggingMessage.getApplicationName());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, loggingMessage.getDeviceIdentification());
    objectMessage.setStringProperty(Constants.RESPONSE_RESULT, loggingMessage.getResponseResult());
    objectMessage.setIntProperty(
        Constants.RESPONSE_DATA_SIZE, loggingMessage.getResponseDataSize());
    return objectMessage;
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
