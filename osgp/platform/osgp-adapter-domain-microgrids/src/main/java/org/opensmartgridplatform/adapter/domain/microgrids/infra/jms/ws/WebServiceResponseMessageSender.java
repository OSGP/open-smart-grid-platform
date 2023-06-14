// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.infra.jms.ws;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

// Send response message to the web service adapter.
@Component(value = "domainMicrogridsOutboundWebServiceResponsesMessageSender")
public class WebServiceResponseMessageSender implements NotificationResponseMessageSender {

  @Autowired
  @Qualifier("domainMicrogridsOutboundWebServiceResponsesJmsTemplate")
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
    final Long originalTimeToLive = this.jmsTemplate.getTimeToLive();
    if (timeToLive != null) {
      // Set the custom time to live.
      this.jmsTemplate.setTimeToLive(timeToLive);
    }

    this.jmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
            objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
            objectMessage.setJMSType(messageType);
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

    if (timeToLive != null) {
      // Restore the time to live from the configuration.
      this.jmsTemplate.setTimeToLive(originalTimeToLive);
    }
  }

  @Override
  public void send(final ResponseMessage responseMessage, final String messageType) {
    this.send(responseMessage, null, messageType);
  }
}
