/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

public class CoreLogItemRequestMessageSender {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CoreLogItemRequestMessageSender.class);

  @Value("${application.createjsonmessage:false}")
  private boolean createJsonMessage;

  @Autowired private JmsTemplate coreLogItemRequestsJmsTemplate;

  public void send(final CoreLogItemRequestMessage coreLogItemRequestMessage) {

    LOGGER.debug("Sending CoreLogItemRequestMessage");

    this.coreLogItemRequestsJmsTemplate.send(
        session -> {
          if (this.isCreateJsonMessage()) {
            return this.getJsonMessage(coreLogItemRequestMessage, session);
          }
          return this.getObjectMessage(coreLogItemRequestMessage, session);
        });
  }

  public TextMessage getJsonMessage(
      final CoreLogItemRequestMessage coreLogItemRequestMessage, final Session session) {
    TextMessage textMessage = null;
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonString = mapper.writeValueAsString(coreLogItemRequestMessage);
      textMessage = session.createTextMessage(jsonString);
    } catch (final Exception e) {
      LOGGER.error("Error creating json message : {}", e.getMessage());
    }
    return textMessage;
  }

  public ObjectMessage getObjectMessage(
      final CoreLogItemRequestMessage coreLogItemRequestMessage, final Session session)
      throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();
    objectMessage.setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
    objectMessage.setStringProperty(
        Constants.DECODED_MESSAGE, coreLogItemRequestMessage.getDecodedMessage());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, coreLogItemRequestMessage.getDeviceIdentification());
    if (coreLogItemRequestMessage.hasOrganisationIdentification()) {
      objectMessage.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION,
          coreLogItemRequestMessage.getOrganisationIdentification());
    }
    objectMessage.setStringProperty(
        Constants.IS_VALID, coreLogItemRequestMessage.isValid().toString());
    objectMessage.setIntProperty(
        Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
        coreLogItemRequestMessage.getPayloadMessageSerializedSize());
    return objectMessage;
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
