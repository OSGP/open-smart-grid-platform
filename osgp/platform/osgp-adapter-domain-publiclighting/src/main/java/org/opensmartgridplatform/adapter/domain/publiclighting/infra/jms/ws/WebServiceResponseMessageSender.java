// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.ws;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

// Send response message to the web service adapter.
@Component(value = "domainPublicLightingOutboundWebServiceResponsesMessageSender")
public class WebServiceResponseMessageSender implements ResponseMessageSender {

  @Autowired
  @Qualifier("domainPublicLightingOutboundWebServiceResponsesJmsTemplate")
  private JmsTemplate webServiceResponsesJmsTemplate;

  /**
   * Send a response message to the web service adapter using a custom time to live.
   *
   * @param responseMessage The response message to send.
   * @param timeToLive The custom time to live value in milliseconds.
   */
  public void send(final ResponseMessage responseMessage, final Long timeToLive) {

    // Keep the original time to live from configuration.
    final Long originalTimeToLive = this.webServiceResponsesJmsTemplate.getTimeToLive();
    if (timeToLive != null) {
      // Set the custom time to live.
      this.webServiceResponsesJmsTemplate.setTimeToLive(timeToLive);
    }

    this.webServiceResponsesJmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
            objectMessage.setJMSType(responseMessage.getMessageType());
            objectMessage.setJMSPriority(responseMessage.getMessagePriority());
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

    if (timeToLive != null) {
      // Restore the time to live from the configuration.
      this.webServiceResponsesJmsTemplate.setTimeToLive(originalTimeToLive);
    }
  }

  @Override
  public void send(final ResponseMessage responseMessage) {
    this.send(responseMessage, null);
  }
}
