/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

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

@Component(value = "protocolDlmsOutboundLogItemRequestsMessageSender")
public class DlmsLogItemRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DlmsLogItemRequestMessageSender.class);

  @Autowired
  @Qualifier("protocolDlmsOutboundLogItemRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(final DlmsLogItemRequestMessage dlmsLogItemRequestMessage) {

    LOGGER.debug("Sending DlmsLogItemRequestMessage");

    this.jmsTemplate.send(new DlmsLogItemRequestMessageCreator(dlmsLogItemRequestMessage));
  }

  private static final class DlmsLogItemRequestMessageCreator implements MessageCreator {

    private final DlmsLogItemRequestMessage dlmsLogItemRequestMessage;

    @Value("${application.createJsonMessage:false}")
    private boolean createJsonMessage;

    public DlmsLogItemRequestMessageCreator(
        final DlmsLogItemRequestMessage dlmsLogItemRequestMessage) {
      this.dlmsLogItemRequestMessage = dlmsLogItemRequestMessage;
    }

    @Override
    public Message createMessage(final Session session) throws JMSException {
      if (this.isCreateJsonMessage()) {
        return this.getJsonMessage(session);
      }
      return this.getObjectMessage(session);
    }

    private TextMessage getJsonMessage(final Session session) {
      TextMessage textMessage = null;
      try {
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.writeValueAsString(this.dlmsLogItemRequestMessage);
        textMessage = session.createTextMessage(jsonString);
      } catch (final Exception e) {
        LOGGER.error("Error creating json message : {}", e.getMessage());
      }
      return textMessage;
    }

    private ObjectMessage getObjectMessage(final Session session) throws JMSException {
      final ObjectMessage objectMessage = session.createObjectMessage();
      objectMessage.setJMSType(Constants.DLMS_LOG_ITEM_REQUEST);
      objectMessage.setStringProperty(
          Constants.IS_INCOMING, this.dlmsLogItemRequestMessage.isIncoming().toString());
      objectMessage.setStringProperty(
          Constants.ENCODED_MESSAGE, this.dlmsLogItemRequestMessage.getEncodedMessage());
      objectMessage.setStringProperty(
          Constants.DECODED_MESSAGE, this.dlmsLogItemRequestMessage.getDecodedMessage());
      objectMessage.setStringProperty(
          Constants.DEVICE_IDENTIFICATION,
          this.dlmsLogItemRequestMessage.getDeviceIdentification());
      if (this.dlmsLogItemRequestMessage.hasOrganisationIdentification()) {
        objectMessage.setStringProperty(
            Constants.ORGANISATION_IDENTIFICATION,
            this.dlmsLogItemRequestMessage.getOrganisationIdentification());
      }
      objectMessage.setStringProperty(
          Constants.IS_VALID, this.dlmsLogItemRequestMessage.isValid().toString());
      objectMessage.setIntProperty(
          Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
          this.dlmsLogItemRequestMessage.getPayloadMessageSerializedSize());
      return objectMessage;
    }

    private boolean isCreateJsonMessage() {
      return this.createJsonMessage;
    }
  }
}
