/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.jms.core.MessageCreator;

public class OsgpCoreRequestMessageCreator implements MessageCreator {

  private RequestMessage requestMessage;
  private String messageType;
  private String ipAddress;
  private Long scheduleTime;

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
    objectMessage.setStringProperty(Constants.IP_ADDRESS, this.ipAddress);
    if (this.scheduleTime != null) {
      objectMessage.setLongProperty(Constants.SCHEDULE_TIME, this.scheduleTime);
    }
    objectMessage.setObject(this.requestMessage.getRequest());

    return objectMessage;
  }
}
