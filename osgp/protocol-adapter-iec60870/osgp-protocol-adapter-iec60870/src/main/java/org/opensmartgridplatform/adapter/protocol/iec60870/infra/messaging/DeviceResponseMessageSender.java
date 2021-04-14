/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessageValidator;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component("protocolIec60870OutboundOsgpCoreResponsesMessageSender")
public class DeviceResponseMessageSender implements ResponseMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

  private static final String LOG_MESSAGE_RESPONSE_MESSAGE_OF_WRONG_TYPE =
      "Only ProtocolResponseMessage type is expected for DeviceResponseMessageSender, received responseMessage of type {}.";
  private static final String LOG_MESSAGE_JMS_EXCEPTION = "JMS Exception, closing all connections.";

  @Autowired
  @Qualifier("protocolIec60870OutboundOsgpCoreResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Autowired private ClientConnectionService iec60870DeviceConnectionService;

  @Autowired private boolean isCloseConnectionsOnBrokerFailure;

  @Override
  public void send(final ResponseMessage responseMessage) {
    if (!(responseMessage instanceof ProtocolResponseMessage)) {
      LOGGER.error(
          LOG_MESSAGE_RESPONSE_MESSAGE_OF_WRONG_TYPE, responseMessage.getClass().getName());
      return;
    }

    final ProtocolResponseMessage msg = (ProtocolResponseMessage) responseMessage;

    if (!ProtocolResponseMessageValidator.isValid(msg, LOGGER)) {
      return;
    }

    try {
      this.sendMessage(msg);
    } catch (final IllegalStateException | UncategorizedJmsException e) {
      /*
       * IllegalStateException occurs when activemq connection pool is
       * exhausted or activemq failover timeout is reached
       * UncategorizedJmsException might also occur when activemq failover
       * timeout is reached
       */
      if (this.isCloseConnectionsOnBrokerFailure) {
        LOGGER.error(LOG_MESSAGE_JMS_EXCEPTION, e);
        this.iec60870DeviceConnectionService.closeAllConnections();
      }
      throw e;
    }
  }

  private void sendMessage(final ProtocolResponseMessage responseMessage) {

    LOGGER.info(
        "Sending protocol response message [correlationUid={}, device={}, messageType={}, messagePriority={}]",
        responseMessage.getCorrelationUid(),
        responseMessage.getDeviceIdentification(),
        responseMessage.getMessageType(),
        responseMessage.getMessagePriority());

    final MessageCreator responseMessageCreator = new ResponseMessageCreator(responseMessage);
    this.jmsTemplate.send(responseMessageCreator);
  }

  private static class ResponseMessageCreator implements MessageCreator {

    private final ProtocolResponseMessage message;

    public ResponseMessageCreator(final ProtocolResponseMessage message) {
      this.message = message;
    }

    @Override
    public Message createMessage(final Session session) throws JMSException {
      final ObjectMessage msg = session.createObjectMessage(this.message);
      msg.setJMSCorrelationID(this.message.getCorrelationUid());
      msg.setStringProperty(Constants.DOMAIN, this.message.getDomain());
      msg.setStringProperty(Constants.DOMAIN_VERSION, this.message.getDomainVersion());
      msg.setJMSType(this.message.getMessageType());
      msg.setJMSPriority(this.message.getMessagePriority());
      msg.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION, this.message.getOrganisationIdentification());
      msg.setStringProperty(
          Constants.DEVICE_IDENTIFICATION, this.message.getDeviceIdentification());
      msg.setStringProperty(Constants.RESULT, this.message.getResult().toString());
      if (this.message.getOsgpException() != null) {
        msg.setStringProperty(Constants.DESCRIPTION, this.message.getOsgpException().getMessage());
      }
      msg.setBooleanProperty(Constants.IS_SCHEDULED, this.message.isScheduled());
      msg.setIntProperty(Constants.RETRY_COUNT, this.message.getRetryCount());
      return msg;
    }
  }
}
