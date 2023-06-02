//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component(value = "protocolIec60870OutboundOsgpCoreRequestsMessageSender")
public class OsgpRequestMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsgpRequestMessageSender.class);

  @Autowired
  @Qualifier("protocolIec60870OutboundOsgpCoreRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(final RequestMessage requestMessage, final String messageType) {
    LOGGER.info("Sending request message to OSGP.");

    this.jmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
          objectMessage.setJMSType(messageType);
          objectMessage.setStringProperty(
              Constants.ORGANISATION_IDENTIFICATION,
              requestMessage.getOrganisationIdentification());
          objectMessage.setStringProperty(
              Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());

          return objectMessage;
        });
  }
}
