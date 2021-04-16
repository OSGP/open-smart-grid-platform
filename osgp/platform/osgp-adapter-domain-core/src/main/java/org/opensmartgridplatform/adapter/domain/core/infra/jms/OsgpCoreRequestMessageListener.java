/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// This class should fetch request messages from incoming requests queue of OSGP Core.
@Component(value = "domainCoreInboundOsgpCoreRequestsMessageListener")
public class OsgpCoreRequestMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OsgpCoreRequestMessageListener.class);

  @Autowired
  private DomainCoreDeviceRequestMessageProcessorMap domainCoreDeviceRequestMessageProcessorMap;

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());

      final ObjectMessage objectMessage = (ObjectMessage) message;
      final MessageType messageType = MessageType.valueOf(objectMessage.getJMSType());

      final MessageProcessor messageProcessor =
          this.domainCoreDeviceRequestMessageProcessorMap.getMessageProcessor(messageType);
      messageProcessor.processMessage(objectMessage);
    } catch (final JMSException e) {
      LOGGER.error("Exception: {}", e.getMessage(), e);
    }
  }
}
