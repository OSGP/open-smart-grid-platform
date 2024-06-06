// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.core;

import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// This class should send response messages to OSGP Core.
@Component(value = "domainDistributionAutomationOutboundOsgpCoreResponsesMessageSender")
public class OsgpCoreResponseMessageSender {

  @Autowired
  @Qualifier("domainDistributionAutomationOutboundOsgpCoreResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(final ResponseMessage responseMessage, final String messageType) {

    this.jmsTemplate.send(
        (final Session session) -> {
          final ObjectMessage objectMessage = session.createObjectMessage();
          objectMessage.setJMSType(messageType);
          objectMessage.setStringProperty(
              Constants.ORGANISATION_IDENTIFICATION,
              responseMessage.getOrganisationIdentification());
          objectMessage.setStringProperty(
              Constants.DEVICE_IDENTIFICATION, responseMessage.getDeviceIdentification());
          objectMessage.setObject(responseMessage);
          return objectMessage;
        });
  }
}
