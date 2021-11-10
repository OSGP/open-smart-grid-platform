/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.infra.jms;

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

  @Value("${auditlogging.message.create.json:false}")
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
    if (loggingMessage == null) {
      LOGGER.error("LoggingMessage is null and will not be send");
    } else {
      LOGGER.debug("Sending logger message");
      final MessageCreator messageCreator;
      if (this.isCreateJsonMessage()) {
        messageCreator = new LoggingJsonMessageCreator(loggingMessage);
      } else {
        messageCreator = new LoggingObjectMessageCreator(loggingMessage);
      }

      this.loggingJmsTemplate.send(messageCreator);
    }
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
