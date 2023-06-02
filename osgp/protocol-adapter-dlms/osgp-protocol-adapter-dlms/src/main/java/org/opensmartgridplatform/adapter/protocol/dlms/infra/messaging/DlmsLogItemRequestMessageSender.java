//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
      return;
    }
    final MessageCreator messageCreator;
    if (this.isCreateJsonMessage()) {
      messageCreator = new DlmsLogItemRequestJsonMessageCreator(dlmsLogItemRequestMessage);
    } else {
      messageCreator = new DlmsLogItemRequestObjectMessageCreator(dlmsLogItemRequestMessage);
    }

    LOGGER.debug("Sending DlmsLogItemRequestMessage");

    this.jmsTemplate.send(messageCreator);
  }

  private boolean isCreateJsonMessage() {
    return this.createJsonMessage;
  }
}
