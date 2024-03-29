// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessageValidator;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolOslpOutboundOsgpCoreResponsesMessageSender")
public class DeviceResponseMessageSender implements ResponseMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

  @Autowired
  @Qualifier("protocolOslpOutboundOsgpCoreResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  public void send(final ResponseMessage responseMessage) {
    if (!(responseMessage instanceof ProtocolResponseMessage)) {
      LOGGER.error("Only ProtocolResponseMessage type is expected for DeviceResponseMessageSender");
      return;
    }

    final ProtocolResponseMessage msg = (ProtocolResponseMessage) responseMessage;

    if (!ProtocolResponseMessageValidator.isValid(msg, LOGGER)) {
      return;
    }

    this.sendMessage(msg);
  }

  private void sendMessage(final ProtocolResponseMessage responseMessage) {

    LOGGER.info(
        "Sending protocol response message for device: {} of message type: {} with message priority: {}",
        responseMessage.getDeviceIdentification(),
        responseMessage.getMessageType(),
        responseMessage.getMessagePriority());

    this.jmsTemplate.send(new DeviceResponseMessageCreator(responseMessage));
  }

  private static class DeviceResponseMessageCreator implements MessageCreator {

    private final ProtocolResponseMessage message;

    public DeviceResponseMessageCreator(final ProtocolResponseMessage message) {
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
