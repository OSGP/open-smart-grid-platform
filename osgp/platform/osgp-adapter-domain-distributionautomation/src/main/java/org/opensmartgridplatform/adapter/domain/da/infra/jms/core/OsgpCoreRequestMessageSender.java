//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core;

import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// Send request message to the requests queue of OSGP Core.
@Component(value = "domainDistributionAutomationOutboundOsgpCoreRequestsMessageSender")
public class OsgpCoreRequestMessageSender {

  @Autowired
  @Qualifier("domainDistributionAutomationOutboundOsgpCoreRequestsJmsTemplate")
  private JmsTemplate jmsTemplate;

  public void send(
      final RequestMessage requestMessage, final String messageType, final String ipAddress) {
    this.send(requestMessage, messageType, ipAddress, null);
  }

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final String ipAddress,
      final Long scheduleTime) {

    this.jmsTemplate.send(
        new OsgpCoreRequestMessageCreator(requestMessage, messageType, ipAddress, scheduleTime));
  }
}
