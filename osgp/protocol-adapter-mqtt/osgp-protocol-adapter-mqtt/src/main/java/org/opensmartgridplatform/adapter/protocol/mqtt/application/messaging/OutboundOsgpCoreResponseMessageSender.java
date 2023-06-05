// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessageValidator;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolMqttOutboundOsgpCoreResponsesMessageSender")
public class OutboundOsgpCoreResponseMessageSender implements ResponseMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundOsgpCoreResponseMessageSender.class);

  private final JmsTemplate protocolMqttOutboundOsgpCoreResponsesJmsTemplate;

  public OutboundOsgpCoreResponseMessageSender(
      final JmsTemplate protocolMqttOutboundOsgpCoreResponsesJmsTemplate) {
    this.protocolMqttOutboundOsgpCoreResponsesJmsTemplate =
        protocolMqttOutboundOsgpCoreResponsesJmsTemplate;
  }

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
    this.protocolMqttOutboundOsgpCoreResponsesJmsTemplate.send(
        new ProtocolResponseMessageCreator(responseMessage));
  }

  private static final class ProtocolResponseMessageCreator implements MessageCreator {

    private final ProtocolResponseMessage responseMessage;

    public ProtocolResponseMessageCreator(final ProtocolResponseMessage responseMessage) {
      this.responseMessage = responseMessage;
    }

    @Override
    public Message createMessage(final Session session) throws JMSException {
      final ObjectMessage objectMessage = session.createObjectMessage(this.responseMessage);
      this.responseMessage.messageMetadata().applyTo(objectMessage);
      objectMessage.setStringProperty(
          Constants.RESULT, this.responseMessage.getResult().toString());
      if (this.responseMessage.getOsgpException() != null) {
        objectMessage.setStringProperty(
            Constants.DESCRIPTION, this.responseMessage.getOsgpException().getMessage());
      }

      if (this.responseMessage.getRetryHeader().shouldRetry()) {
        objectMessage.setIntProperty(
            Constants.MAX_RETRIES, this.responseMessage.getRetryHeader().getMaxRetries());
        objectMessage.setLongProperty(
            Constants.SCHEDULE_TIME,
            this.responseMessage.getRetryHeader().getScheduledRetryTime().getTime());
      }

      return objectMessage;
    }
  }
}
