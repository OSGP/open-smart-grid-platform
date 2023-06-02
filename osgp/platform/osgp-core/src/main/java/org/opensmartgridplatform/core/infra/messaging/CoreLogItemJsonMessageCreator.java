//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.jms.core.MessageCreator;

public class CoreLogItemJsonMessageCreator implements MessageCreator {

  private final CoreLogItemRequestMessage coreLogItemRequestMessage;

  public CoreLogItemJsonMessageCreator(final CoreLogItemRequestMessage coreLogItemRequestMessage) {
    this.coreLogItemRequestMessage = coreLogItemRequestMessage;
  }

  @Override
  public Message createMessage(final Session session) throws JMSException {
    return this.getJsonMessage(session);
  }

  public TextMessage getJsonMessage(final Session session) throws JMSException {
    TextMessage textMessage = null;
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonString = mapper.writeValueAsString(this.coreLogItemRequestMessage);
      textMessage = session.createTextMessage(jsonString);
      textMessage.setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
    } catch (final JsonProcessingException e) {
      throw new UncheckedIOException("Error processing log item as JSON text", e);
    }
    return textMessage;
  }
}
