// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms;

import jakarta.jms.ObjectMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/** Class for sending smart metering request messages to a queue */
@Component(value = "wsSmartMeteringOutboundDomainRequestsMessageSender")
public class SmartMeteringRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SmartMeteringRequestMessageSender.class);

  @Autowired
  @Qualifier("wsSmartMeteringOutboundDomainRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  /**
   * Method for sending a request message to the queue
   *
   * @param requestMessage The SmartMeteringRequestMessage request message to send.
   */
  public void send(final SmartMeteringRequestMessage requestMessage) {
    LOGGER.debug("Sending smart metering request message to the queue");

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
   * Method for sending a request message to the smart metering requests queue
   *
   * @param requestMessage The SmartMeteringRequestMessage request message to send.
   */
  private void sendMessage(final SmartMeteringRequestMessage requestMessage) {
    LOGGER.info("Sending message to the smart metering requests queue");

    this.jmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage =
              session.createObjectMessage(requestMessage.getRequest());
          requestMessage.messageMetadata().applyTo(objectMessage);
          return objectMessage;
        });
  }
}
