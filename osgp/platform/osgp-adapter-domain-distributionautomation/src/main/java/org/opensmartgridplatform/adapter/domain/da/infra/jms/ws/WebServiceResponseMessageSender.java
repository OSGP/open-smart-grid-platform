// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws;

import org.opensmartgridplatform.adapter.domain.da.infra.jms.BaseResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// Send response message to the web service adapter.
@Component(value = "domainDistributionAutomationOutboundWebServiceResponsesMessageSender")
public class WebServiceResponseMessageSender extends BaseResponseMessageSender
    implements NotificationResponseMessageSender {

  @Autowired
  @Qualifier("domainDistributionAutomationOutboundWebServiceResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  public void send(final ResponseMessage responseMessage, final String messageType) {
    this.send(this.jmsTemplate, responseMessage, null, messageType);
  }
}
