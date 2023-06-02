//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceConnectionService;
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

@Component(value = "protocolIec61850OutboundOsgpCoreResponsesMessageSender")
public class DeviceResponseMessageSender implements ResponseMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

  private static final String LOG_MESSAGE_RESPONSE_MESSAGE_OF_WRONG_TYPE =
      "Only ProtocolResponseMessage type is "
          + "expected for DeviceResponseMessageSender, received responseMessage of type {}.";
  private static final String LOG_MESSAGE_JMS_EXCEPTION = "JMS Exception, closing all connections.";

  @Autowired
  @Qualifier("protocolIec61850OutboundOsgpCoreResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Autowired private Iec61850DeviceConnectionService iec61850deviceConnectionService;

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
        this.iec61850deviceConnectionService.closeAllConnections();
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

    this.jmsTemplate.send(new DeviceResponseMessageCreater(responseMessage));
  }

  private static class DeviceResponseMessageCreater implements MessageCreator {

    private final ProtocolResponseMessage message;

    public DeviceResponseMessageCreater(final ProtocolResponseMessage message) {
      this.message = message;
    }

    @Override
    public Message createMessage(final Session session) throws JMSException {
      final ObjectMessage objMsg = session.createObjectMessage(this.message);
      objMsg.setJMSCorrelationID(this.message.getCorrelationUid());
      objMsg.setStringProperty(Constants.DOMAIN, this.message.getDomain());
      objMsg.setStringProperty(Constants.DOMAIN_VERSION, this.message.getDomainVersion());
      objMsg.setJMSType(this.message.getMessageType());
      objMsg.setJMSPriority(this.message.getMessagePriority());
      objMsg.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION, this.message.getOrganisationIdentification());
      objMsg.setStringProperty(
          Constants.DEVICE_IDENTIFICATION, this.message.getDeviceIdentification());
      objMsg.setStringProperty(Constants.RESULT, this.message.getResult().toString());
      if (this.message.getOsgpException() != null) {
        objMsg.setStringProperty(
            Constants.DESCRIPTION, this.message.getOsgpException().getMessage());
      }
      objMsg.setBooleanProperty(Constants.IS_SCHEDULED, this.message.isScheduled());
      objMsg.setIntProperty(Constants.RETRY_COUNT, this.message.getRetryCount());
      return objMsg;
    }
  }
}
