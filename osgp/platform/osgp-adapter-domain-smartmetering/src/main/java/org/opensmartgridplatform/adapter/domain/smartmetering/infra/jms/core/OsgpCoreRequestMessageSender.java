/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core;

import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// Send request message to the requests queue of OSGP Core.
@Component(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
public class OsgpCoreRequestMessageSender {

  @Autowired
  @Qualifier("domainSmartMeteringOutboundOsgpCoreRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final int messagePriority,
      final Long scheduleTime,
      final boolean bypassRetry) {

    this.jmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage = session.createObjectMessage();

          objectMessage.setJMSType(messageType);
          objectMessage.setJMSPriority(messagePriority);
          objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
          objectMessage.setStringProperty(
              Constants.ORGANISATION_IDENTIFICATION,
              requestMessage.getOrganisationIdentification());
          objectMessage.setStringProperty(
              Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
          objectMessage.setBooleanProperty(Constants.BYPASS_RETRY, bypassRetry);
          if (scheduleTime != null) {
            objectMessage.setLongProperty(Constants.SCHEDULE_TIME, scheduleTime);
          }
          objectMessage.setStringProperty(Constants.IP_ADDRESS, requestMessage.getIpAddress());
          objectMessage.setObject(requestMessage.getRequest());

          return objectMessage;
        });
  }
}
