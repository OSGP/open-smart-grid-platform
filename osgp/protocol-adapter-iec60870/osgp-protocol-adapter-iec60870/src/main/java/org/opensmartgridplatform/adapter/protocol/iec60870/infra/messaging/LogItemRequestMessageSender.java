// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolIec60870OutboundLogItemRequestsMessageSender")
public class LogItemRequestMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogItemRequestMessageSender.class);

  private static final String DEFAULT_ENCODED_MESSAGE = null;
  private static final String DEFAULT_VALID = "true";
  private static final int DEFAULT_SIZE = 0;

  @Autowired
  @Qualifier("protocolIec60870OutboundLogItemRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(final LogItem logItem) {

    LOGGER.debug("Sending LogItemRequestMessage");

    final LogItemMessageCreator messageCreator = new LogItemMessageCreator(logItem);
    this.jmsTemplate.send(messageCreator);
  }

  private static class LogItemMessageCreator implements MessageCreator {

    private final LogItem logItem;

    public LogItemMessageCreator(final LogItem logItem) {
      this.logItem = logItem;
    }

    @Override
    public Message createMessage(final Session session) throws JMSException {
      final ObjectMessage objectMessage = session.createObjectMessage();
      objectMessage.setJMSType(Constants.IEC60870_LOG_ITEM_REQUEST);
      objectMessage.setStringProperty(Constants.IS_INCOMING, this.logItem.isIncoming().toString());
      objectMessage.setStringProperty(Constants.DECODED_MESSAGE, this.logItem.getMessage());
      objectMessage.setStringProperty(
          Constants.DEVICE_IDENTIFICATION, this.logItem.getDeviceIdentification());
      objectMessage.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION, this.logItem.getOrganisationIdentification());
      // Properties below are expected by OSGP Logging component, but
      // currently not used in this protocol adapter, therefore default
      // values are passed.
      objectMessage.setStringProperty(Constants.ENCODED_MESSAGE, DEFAULT_ENCODED_MESSAGE);
      objectMessage.setStringProperty(Constants.IS_VALID, DEFAULT_VALID);
      objectMessage.setIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE, DEFAULT_SIZE);
      return objectMessage;
    }
  }
}
