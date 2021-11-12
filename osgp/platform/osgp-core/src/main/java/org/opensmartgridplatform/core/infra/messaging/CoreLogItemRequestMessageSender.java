/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class CoreLogItemRequestMessageSender {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CoreLogItemRequestMessageSender.class);

  @Value("${auditlogging.message.create.json:false}")
  private boolean createJsonMessage;

  @Autowired private JmsTemplate coreLogItemRequestsJmsTemplate;

  public void send(final CoreLogItemRequestMessage coreLogItemRequestMessage) {
    if (coreLogItemRequestMessage == null) {
      LOGGER.error("CoreLogItemRequestMessage is null and will not be send");
      return;
    }
    LOGGER.debug("Sending CoreLogItemRequestMessage");
    final MessageCreator messageCreator;
    if (this.isCreateJsonMessage()) {
      messageCreator = new CoreLogItemJsonMessageCreator(coreLogItemRequestMessage);
    } else {
      messageCreator = new CoreLogItemJsonMessageCreator(coreLogItemRequestMessage);
    }

    this.coreLogItemRequestsJmsTemplate.send(messageCreator);
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
