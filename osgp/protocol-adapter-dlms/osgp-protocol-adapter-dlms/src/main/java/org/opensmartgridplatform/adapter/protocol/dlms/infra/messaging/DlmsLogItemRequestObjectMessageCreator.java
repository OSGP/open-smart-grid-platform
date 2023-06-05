// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.jms.core.MessageCreator;

public class DlmsLogItemRequestObjectMessageCreator implements MessageCreator {
  private final DlmsLogItemRequestMessage dlmsLogItemRequestMessage;

  @Override
  public Message createMessage(final Session session) throws JMSException {
    return this.getObjectMessage(session);
  }

  public DlmsLogItemRequestObjectMessageCreator(
      final DlmsLogItemRequestMessage dlmsLogItemRequestMessage) {
    this.dlmsLogItemRequestMessage = dlmsLogItemRequestMessage;
  }

  public ObjectMessage getObjectMessage(final Session session) throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();
    objectMessage.setJMSType(Constants.DLMS_LOG_ITEM_REQUEST);
    objectMessage.setStringProperty(
        Constants.IS_INCOMING, this.dlmsLogItemRequestMessage.isIncoming().toString());
    objectMessage.setStringProperty(
        Constants.ENCODED_MESSAGE, this.dlmsLogItemRequestMessage.getEncodedMessage());
    objectMessage.setStringProperty(
        Constants.DECODED_MESSAGE, this.dlmsLogItemRequestMessage.getDecodedMessage());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, this.dlmsLogItemRequestMessage.getDeviceIdentification());
    if (this.dlmsLogItemRequestMessage.hasOrganisationIdentification()) {
      objectMessage.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION,
          this.dlmsLogItemRequestMessage.getOrganisationIdentification());
    }
    objectMessage.setStringProperty(
        Constants.IS_VALID, this.dlmsLogItemRequestMessage.isValid().toString());
    objectMessage.setIntProperty(
        Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
        this.dlmsLogItemRequestMessage.getPayloadMessageSerializedSize());
    return objectMessage;
  }
}
