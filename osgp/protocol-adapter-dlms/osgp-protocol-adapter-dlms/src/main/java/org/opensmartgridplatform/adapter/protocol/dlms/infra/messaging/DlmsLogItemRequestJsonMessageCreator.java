// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.jms.core.MessageCreator;

public class DlmsLogItemRequestJsonMessageCreator implements MessageCreator {

  private final DlmsLogItemRequestMessage dlmsLogItemRequestMessage;

  public DlmsLogItemRequestJsonMessageCreator(
      final DlmsLogItemRequestMessage dlmsLogItemRequestMessage) {
    this.dlmsLogItemRequestMessage = dlmsLogItemRequestMessage;
  }

  @Override
  public Message createMessage(final Session session) throws JMSException {
    return this.getJsonMessage(session);
  }

  public TextMessage getJsonMessage(final Session session) throws JMSException {
    TextMessage textMessage = null;
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonString = mapper.writeValueAsString(this.dlmsLogItemRequestMessage);
      textMessage = session.createTextMessage(jsonString);
      textMessage.setJMSType(Constants.DLMS_LOG_ITEM_REQUEST);
    } catch (final JsonProcessingException e) {
      throw new UncheckedIOException("Error processing log item as JSON text", e);
    }
    return textMessage;
  }
}
