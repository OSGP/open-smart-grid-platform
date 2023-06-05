// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// Send response message to the web service adapter.
@Component(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
public class WebServiceResponseMessageSender implements NotificationResponseMessageSender {

  @Autowired
  @Qualifier("domainSmartMeteringOutboundWebServiceResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  /**
   * Send a response message to the web service adapter using a custom time to live.
   *
   * @param responseMessage The response message to send.
   * @param timeToLive The custom time to live value in milliseconds.
   */
  public void send(
      final ResponseMessage responseMessage, final Long timeToLive, final String messageType) {

    // Keep the original time to live from configuration.
    final long originalTimeToLive = this.jmsTemplate.getTimeToLive();
    if (timeToLive != null) {
      // Set the custom time to live.
      this.jmsTemplate.setTimeToLive(timeToLive);
    }

    this.jmsTemplate.send(session -> this.makeObjectMessage(session, responseMessage, messageType));

    if (timeToLive != null) {
      // Restore the time to live from the configuration.
      this.jmsTemplate.setTimeToLive(originalTimeToLive);
    }
  }

  private ObjectMessage makeObjectMessage(
      final Session session, final ResponseMessage responseMessage, final String messageType)
      throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
    responseMessage.messageMetadata().applyTo(objectMessage);

    objectMessage.setJMSType(messageType);
    objectMessage.setStringProperty(Constants.RESULT, responseMessage.getResult().toString());
    if (responseMessage.getOsgpException() == null) {
      objectMessage.setObject(responseMessage.getDataObject());
    } else {
      String description;

      // If an exception had a cause, get the message of the
      // cause. If not, get the message of the exception itself
      final OsgpException osgpException = responseMessage.getOsgpException();
      if (osgpException.getCause() != null) {
        description = osgpException.getCause().getMessage();
      } else {
        description = osgpException.getMessage();
      }
      objectMessage.setStringProperty(Constants.DESCRIPTION, description);
      objectMessage.setObject(osgpException);
    }

    return objectMessage;
  }

  @Override
  public void send(final ResponseMessage responseMessage, final String messageType) {
    this.send(responseMessage, null, messageType);
  }
}
