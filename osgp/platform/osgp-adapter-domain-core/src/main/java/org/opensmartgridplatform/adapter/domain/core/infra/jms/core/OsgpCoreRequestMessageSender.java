/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ScheduledMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

// Send request message to the requests queue of OSGP Core.
@Component(value = "domainCoreOutboundOsgpCoreRequestsMessageSender")
public class OsgpCoreRequestMessageSender {

  @Autowired
  @Qualifier("domainCoreOutboundOsgpCoreRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final String ipAddress) {
    this.send(requestMessage, messageType, messagePriority, ipAddress, null, null);
  }

  public void sendWithScheduledTime(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final String ipAddress,
      final Long scheduledTime) {
    this.send(requestMessage, messageType, messagePriority, ipAddress, scheduledTime, null);
  }

  public void sendWithDelay(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final String ipAddress,
      final Long delay) {
    this.send(requestMessage, messageType, messagePriority, ipAddress, null, delay);
  }

  private void send(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final String ipAddress,
      final Long scheduledTime,
      final Long delay) {

    final String correlationUid = requestMessage.getCorrelationUid();
    final String organisationIdentification = requestMessage.getOrganisationIdentification();
    final String deviceIdentification = requestMessage.getDeviceIdentification();

    this.jmsTemplate.send(
        new MessageCreator() {
          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setJMSType(messageType);
            objectMessage.setJMSPriority(messagePriority);
            objectMessage.setJMSCorrelationID(correlationUid);
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION, organisationIdentification);
            objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION, deviceIdentification);
            objectMessage.setStringProperty(Constants.IP_ADDRESS, ipAddress);
            if (scheduledTime != null) {
              objectMessage.setLongProperty(Constants.SCHEDULE_TIME, scheduledTime);
            }
            if (delay != null) {
              objectMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
            }
            objectMessage.setObject(requestMessage.getRequest());
            return objectMessage;
          }
        });
  }
}
