/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "protocolMqttInboundOsgpCoreRequestsMessageListener")
public class InboundOsgpCoreRequestMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundOsgpCoreRequestMessageListener.class);

  private final MessageProcessorMap protocolMqttInboundOsgpCoreRequestsMessageProcessorMap;

  public InboundOsgpCoreRequestMessageListener(
      final MessageProcessorMap protocolMqttInboundOsgpCoreRequestsMessageProcessorMap) {
    this.protocolMqttInboundOsgpCoreRequestsMessageProcessorMap =
        protocolMqttInboundOsgpCoreRequestsMessageProcessorMap;
  }

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());

      final ObjectMessage objectMessage = (ObjectMessage) message;

      final MessageProcessor processor =
          this.protocolMqttInboundOsgpCoreRequestsMessageProcessorMap.getMessageProcessor(
              objectMessage);

      processor.processMessage(objectMessage);

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
