/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.infra.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.jms.core.MessageCreator;

public class LoggingJsonMessageCreator implements MessageCreator {
  private final LoggingRequestMessage loggingRequestMessage;

  public LoggingJsonMessageCreator(final LoggingRequestMessage loggingRequestMessage) {
    this.loggingRequestMessage = loggingRequestMessage;
  }

  @Override
  public Message createMessage(final Session session) throws JMSException {
    return this.getJsonMessage(session);
  }

  public TextMessage getJsonMessage(final Session session) throws JMSException {
    TextMessage textMessage = null;
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonString = mapper.writeValueAsString(this.loggingRequestMessage);
      textMessage = session.createTextMessage(jsonString);
      textMessage.setJMSType(Constants.LOG_ITEM_REQUEST);
      textMessage.setJMSCorrelationID(this.loggingRequestMessage.getCorrelationUid());
    } catch (final JsonProcessingException e) {
      throw new UncheckedIOException("Error processing log item as JSON text", e);
    }
    return textMessage;
  }
}
