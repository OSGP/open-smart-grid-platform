/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

// Send request message to the requests queue of OSGP Core.
@Component(value = "domainPublicLightingOutboundOsgpCoreRequestsMessageSender")
public class OsgpCoreRequestMessageSender {

  @Autowired
  @Qualifier("domainPublicLightingOutboundOsgpCoreRequestsJmsTemplate")
  private JmsTemplate osgpCoreRequestsJmsTemplate;

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final String ipAddress) {
    this.send(requestMessage, messageType, messagePriority, ipAddress, null);
  }

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final String ipAddress,
      final Long scheduleTime) {

    this.osgpCoreRequestsJmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage();

            objectMessage.setJMSType(messageType);
            objectMessage.setJMSPriority(messagePriority);
            objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION,
                requestMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(
                Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
            objectMessage.setStringProperty(Constants.IP_ADDRESS, ipAddress);
            if (scheduleTime != null) {
              objectMessage.setLongProperty(Constants.SCHEDULE_TIME, scheduleTime);
            }
            objectMessage.setObject(requestMessage.getRequest());

            return objectMessage;
          }
        });
  }
}
