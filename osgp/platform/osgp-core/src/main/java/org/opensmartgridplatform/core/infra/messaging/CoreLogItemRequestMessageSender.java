//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
      messageCreator = new CoreLogItemObjectMessageCreator(coreLogItemRequestMessage);
    }

    this.coreLogItemRequestsJmsTemplate.send(messageCreator);
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
