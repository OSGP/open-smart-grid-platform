//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.infra.jms.ws;

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

// Send request message to the requests queue of web service adapter.
@Component(value = "domainCoreOutboundWebServiceRequestsMessageSender")
public class WebServiceRequestMessageSender {

  @Autowired
  @Qualifier("domainCoreOutboundWebServiceRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(final RequestMessage requestMessage, final String messageType) {

    this.jmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
            objectMessage.setJMSType(messageType);
            objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION,
                requestMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(
                Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
            return objectMessage;
          }
        });
  }
}
