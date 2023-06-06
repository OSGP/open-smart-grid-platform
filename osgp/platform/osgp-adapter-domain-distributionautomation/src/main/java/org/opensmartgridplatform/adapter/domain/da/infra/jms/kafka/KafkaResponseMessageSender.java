// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.kafka;

import org.opensmartgridplatform.adapter.domain.da.infra.jms.BaseResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// Send response message to the kafka adapter.
@Component(value = "domainDistributionAutomationOutboundKafkaResponsesMessageSender")
public class KafkaResponseMessageSender extends BaseResponseMessageSender
    implements NotificationResponseMessageSender {

  @Autowired
  @Qualifier("domainDistributionAutomationOutboundKafkaResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  public void send(final ResponseMessage responseMessage, final String messageType) {
    this.send(this.jmsTemplate, responseMessage, null, messageType);
  }
}
