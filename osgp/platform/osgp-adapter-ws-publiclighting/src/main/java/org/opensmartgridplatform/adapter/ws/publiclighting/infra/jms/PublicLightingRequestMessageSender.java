// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/** Class for sending public lighting request messages to a queue */
@Component(value = "wsPublicLightingOutboundDomainRequestsMessageSender")
public class PublicLightingRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingRequestMessageSender.class);

  @Autowired
  @Qualifier("wsPublicLightingOutboundDomainRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  /**
   * Method for sending a request message to the queue
   *
   * @param requestMessage The PublicLightingRequestMessage request message to send.
   */
  public void send(final PublicLightingRequestMessage requestMessage) {
    LOGGER.debug("Sending public lighting request message to the queue");

    if (requestMessage.getMessageType() == null) {
      LOGGER.error("MessageType is null");
      return;
    }
    if (StringUtils.isBlank(requestMessage.getOrganisationIdentification())) {
      LOGGER.error("OrganisationIdentification is blank");
      return;
    }
    if (StringUtils.isBlank(requestMessage.getDeviceIdentification())) {
      LOGGER.error("DeviceIdentification is blank");
      return;
    }
    if (StringUtils.isBlank(requestMessage.getCorrelationUid())) {
      LOGGER.error("CorrelationUid is blank");
      return;
    }

    this.sendMessage(requestMessage);
  }

  /**
   * Method for sending a request message to the public lighting requests queue
   *
   * @param requestMessage The PublicLightingRequestMessage request message to send.
   */
  private void sendMessage(final PublicLightingRequestMessage requestMessage) {
    LOGGER.info("Sending message to the public lighting requests queue");

    this.jmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage =
                session.createObjectMessage(requestMessage.getRequest());
            objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
            objectMessage.setJMSType(requestMessage.getMessageType().name());
            objectMessage.setJMSPriority(requestMessage.getMessagePriority());
            objectMessage.setStringProperty(
                Constants.ORGANISATION_IDENTIFICATION,
                requestMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(
                Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
            if (requestMessage.getScheduleTime() != null) {
              objectMessage.setLongProperty(
                  Constants.SCHEDULE_TIME,
                  requestMessage.getScheduleTime().toInstant().toEpochMilli());
            }
            return objectMessage;
          }
        });
  }
}
