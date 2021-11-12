/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.jms.core.MessageCreator;

public class LoggingObjectMessageCreator implements MessageCreator {
  private final LoggingRequestMessage loggingRequestMessage;

  public LoggingObjectMessageCreator(final LoggingRequestMessage loggingRequestMessage) {
    this.loggingRequestMessage = loggingRequestMessage;
  }

  @Override
  public Message createMessage(final Session session) throws JMSException {
    return this.getObjectMessage(session, this.loggingRequestMessage);
  }

  public ObjectMessage getObjectMessage(
      final Session session, final LoggingRequestMessage loggingMessage) throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();
    objectMessage.setJMSCorrelationID(loggingMessage.getCorrelationUid());
    objectMessage.setJMSType(Constants.LOG_ITEM_REQUEST);
    objectMessage.setLongProperty(Constants.TIME_STAMP, loggingMessage.getTimeStamp().getTime());
    objectMessage.setStringProperty(Constants.CLASS_NAME, loggingMessage.getClassName());
    objectMessage.setStringProperty(Constants.METHOD_NAME, loggingMessage.getMethodName());
    objectMessage.setStringProperty(
        Constants.ORGANISATION_IDENTIFICATION, loggingMessage.getOrganisationIdentification());
    objectMessage.setStringProperty(Constants.USER_NAME, loggingMessage.getUserName());
    objectMessage.setStringProperty(
        Constants.APPLICATION_NAME, loggingMessage.getApplicationName());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, loggingMessage.getDeviceIdentification());
    objectMessage.setStringProperty(Constants.RESPONSE_RESULT, loggingMessage.getResponseResult());
    objectMessage.setIntProperty(
        Constants.RESPONSE_DATA_SIZE, loggingMessage.getResponseDataSize());
    return objectMessage;
  }
}
