/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.admin.infra.jms.ws;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

// Send response message to the web service adapter.
@Component(value = "domainAdminOutboundWebServiceResponsesMessageSender")
public class WebServiceResponseMessageSender implements ResponseMessageSender {

  @Autowired
  @Qualifier("domainAdminOutboundWebServiceResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  public void send(final ResponseMessage responseMessage) {

    this.jmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
            objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION,
                responseMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(
                Constants.DEVICE_IDENTIFICATION, responseMessage.getDeviceIdentification());
            objectMessage.setStringProperty(
                Constants.RESULT, responseMessage.getResult().toString());
            if (responseMessage.getOsgpException() != null) {
              objectMessage.setStringProperty(
                  Constants.DESCRIPTION, responseMessage.getOsgpException().getMessage());
            }

            return objectMessage;
          }
        });
  }
}
