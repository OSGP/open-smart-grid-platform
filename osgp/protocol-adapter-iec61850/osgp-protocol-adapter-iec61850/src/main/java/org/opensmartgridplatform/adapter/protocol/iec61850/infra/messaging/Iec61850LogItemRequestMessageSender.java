/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolIec61850OutboundLogItemRequestsMessageSender")
public class Iec61850LogItemRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850LogItemRequestMessageSender.class);

  @Autowired
  @Qualifier("protocolIec61850OutboundLogItemRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(final Iec61850LogItemRequestMessage message) {

    LOGGER.debug("Sending Iec61850LogItemRequestMessage");

    this.jmsTemplate.send(
        new MessageCreator() {
          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setJMSType(Constants.IEC61850_LOG_ITEM_REQUEST);
            objectMessage.setStringProperty(Constants.IS_INCOMING, message.isIncoming().toString());
            objectMessage.setStringProperty(Constants.ENCODED_MESSAGE, message.getEncodedMessage());
            objectMessage.setStringProperty(Constants.DECODED_MESSAGE, message.getDecodedMessage());
            objectMessage.setStringProperty(
                Constants.DEVICE_IDENTIFICATION, message.getDeviceIdentification());
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION, message.getOrganisationIdentification());
            objectMessage.setStringProperty(Constants.IS_VALID, message.isValid().toString());
            objectMessage.setIntProperty(
                Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
                message.getPayloadMessageSerializedSize());
            return objectMessage;
          }
        });
  }
}
