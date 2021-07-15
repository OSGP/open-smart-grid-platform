/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "protocolDlmsInboundOsgpCoreResponsesMessageListener")
public class OsgpResponseMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponseMessageListener.class);

  @Autowired
  @Qualifier("protocolDlmsInboundOsgpResponsesMessageProcessorMap")
  private MessageProcessorMap messageProcessorMap;

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info(
          "[{}] - Received message of type: {}",
          message.getJMSCorrelationID(),
          message.getJMSType());

      final MessageMetadata metadata = MessageMetadata.fromMessage(message);

      final ObjectMessage objectMessage = (ObjectMessage) message;
      final MessageProcessor messageProcessor =
          this.messageProcessorMap.getMessageProcessor(objectMessage);

      if (messageProcessor != null) {
        messageProcessor.processMessage(objectMessage);
      } else {
        LOGGER.error(
            "[{}] - Unknown messagetype {}",
            metadata.getCorrelationUid(),
            metadata.getMessageType());
      }

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
