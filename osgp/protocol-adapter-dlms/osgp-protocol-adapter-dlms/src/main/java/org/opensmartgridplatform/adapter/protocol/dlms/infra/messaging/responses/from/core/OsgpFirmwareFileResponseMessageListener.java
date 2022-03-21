/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.processors.GetFirmwareFileResponseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "protocolDlmsInboundOsgpCoreFirmwareFileResponsesMessageListener")
public class OsgpFirmwareFileResponseMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OsgpFirmwareFileResponseMessageListener.class);

  @Autowired
  private GetFirmwareFileResponseMessageProcessor getFirmwareFileResponseMessageProcessor;

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info(
          "[{}] - Received message of type {} on Reply-to queue for Firmware file responses.",
          message.getJMSCorrelationID(),
          message.getJMSType());

      final MessageType messageType = MessageType.valueOf(message.getJMSType());

      if (!messageType.equals(MessageType.GET_FIRMWARE_FILE)) {
        LOGGER.error(
            "Message of type {} can not be handled by Firmware file response processor.",
            message.getJMSType());
      } else {
        final ObjectMessage objectMessage = (ObjectMessage) message;
        this.getFirmwareFileResponseMessageProcessor.processMessage(objectMessage);
      }
    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
