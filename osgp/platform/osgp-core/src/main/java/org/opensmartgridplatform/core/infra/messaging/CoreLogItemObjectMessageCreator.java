// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.jms.core.MessageCreator;

public class CoreLogItemObjectMessageCreator implements MessageCreator {
  private final CoreLogItemRequestMessage coreLogItemRequestMessage;

  public CoreLogItemObjectMessageCreator(
      final CoreLogItemRequestMessage coreLogItemRequestMessage) {
    this.coreLogItemRequestMessage = coreLogItemRequestMessage;
  }

  @Override
  public Message createMessage(final Session session) throws JMSException {
    return this.getObjectMessage(session);
  }

  public ObjectMessage getObjectMessage(final Session session) throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();
    objectMessage.setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
    objectMessage.setStringProperty(
        Constants.DECODED_MESSAGE, this.coreLogItemRequestMessage.getDecodedMessage());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, this.coreLogItemRequestMessage.getDeviceIdentification());
    if (this.coreLogItemRequestMessage.hasOrganisationIdentification()) {
      objectMessage.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION,
          this.coreLogItemRequestMessage.getOrganisationIdentification());
    }
    objectMessage.setStringProperty(
        Constants.IS_VALID, this.coreLogItemRequestMessage.isValid().toString());
    objectMessage.setIntProperty(
        Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
        this.coreLogItemRequestMessage.getPayloadMessageSerializedSize());
    return objectMessage;
  }
}
