// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.infra.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/** Class for sending distribution automation request messages to a queue */
@Component("wsDistributionAutomationOutboundDomainRequestsMessageSender")
public class DistributionAutomationRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationRequestMessageSender.class);

  @Autowired
  @Qualifier("wsDistributionAutomationOutboundDomainRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  /**
   * Method for sending a request message to the queue
   *
   * @param requestMessage The DistributionAutomationRequestMessage request message to send.
   * @throws ArgumentNullOrEmptyException
   */
  public void send(final DistributionAutomationRequestMessage requestMessage)
      throws ArgumentNullOrEmptyException {
    LOGGER.debug("Sending distribution automation request message to the queue");

    if (requestMessage.getMessageType() == null) {
      LOGGER.error("MessageType is null");
      throw new ArgumentNullOrEmptyException("MessageType");
    }
    if (StringUtils.isBlank(requestMessage.getOrganisationIdentification())) {
      LOGGER.error("OrganisationIdentification is blank");
      throw new ArgumentNullOrEmptyException("OrganisationIdentification");
    }
    if (StringUtils.isBlank(requestMessage.getDeviceIdentification())) {
      LOGGER.error("DeviceIdentification is blank");
      throw new ArgumentNullOrEmptyException("DeviceIdentification");
    }
    if (StringUtils.isBlank(requestMessage.getCorrelationUid())) {
      LOGGER.error("CorrelationUid is blank");
      throw new ArgumentNullOrEmptyException("CorrelationUid");
    }

    this.sendMessage(requestMessage);
  }

  /**
   * Method for sending a request message to the da requests queue
   *
   * @param requestMessage The DistributionAutomationRequestMessage request message to send.
   */
  private void sendMessage(final DistributionAutomationRequestMessage requestMessage) {
    LOGGER.info("Sending message to the da requests queue");

    this.jmsTemplate.send(
        new MessageCreator() {

          @Override
          public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage =
                session.createObjectMessage(requestMessage.getRequest());
            objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
            objectMessage.setJMSType(requestMessage.getMessageType().name());
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
