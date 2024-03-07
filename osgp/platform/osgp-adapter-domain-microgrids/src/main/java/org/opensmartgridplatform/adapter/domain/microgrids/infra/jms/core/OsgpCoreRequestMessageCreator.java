// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.infra.jms.core;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.jms.core.MessageCreator;

public class OsgpCoreRequestMessageCreator implements MessageCreator {

  private final RequestMessage requestMessage;
  private final String messageType;
  private final String ipAddress;
  private final Long scheduleTime;

  public OsgpCoreRequestMessageCreator(
      final RequestMessage requestMessage,
      final String messageType,
      final String ipAddress,
      final Long scheduleTime) {
    this.requestMessage = requestMessage;
    this.messageType = messageType;
    this.ipAddress = ipAddress;
    this.scheduleTime = scheduleTime;
  }

  @Override
  public Message createMessage(final Session session) throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();

    objectMessage.setJMSType(this.messageType);
    objectMessage.setJMSCorrelationID(this.requestMessage.getCorrelationUid());
    objectMessage.setStringProperty(
        Constants.ORGANISATION_IDENTIFICATION, this.requestMessage.getOrganisationIdentification());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, this.requestMessage.getDeviceIdentification());
    objectMessage.setStringProperty(Constants.NETWORK_ADDRESS, this.ipAddress);
    if (this.scheduleTime != null) {
      objectMessage.setLongProperty(Constants.SCHEDULE_TIME, this.scheduleTime);
    }
    objectMessage.setObject(this.requestMessage.getRequest());

    return objectMessage;
  }
}
