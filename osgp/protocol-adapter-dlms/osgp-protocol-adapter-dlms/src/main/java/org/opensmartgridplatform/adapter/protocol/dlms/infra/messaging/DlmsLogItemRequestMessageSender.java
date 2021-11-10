/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolDlmsOutboundLogItemRequestsMessageSender")
public class DlmsLogItemRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DlmsLogItemRequestMessageSender.class);

  @Autowired
  @Qualifier("protocolDlmsOutboundLogItemRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Value("${auditlogging.message.create.json:false}")
  private boolean createJsonMessage;

  public void send(final DlmsLogItemRequestMessage dlmsLogItemRequestMessage) {
    if (dlmsLogItemRequestMessage == null) {
      LOGGER.error("dlmsLogItemRequestMessage is null and will not be send");
    } else {
      final MessageCreator messageCreator;
      if (this.isCreateJsonMessage()) {
        messageCreator = new DlmsLogItemRequestJsonMessageCreator(dlmsLogItemRequestMessage);
      } else {
        messageCreator = new DlmsLogItemRequestObjectMessageCreator(dlmsLogItemRequestMessage);
      }

      LOGGER.debug("Sending DlmsLogItemRequestMessage");

      this.jmsTemplate.send(messageCreator);
    }
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
