// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.kafka.da.infra.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.kafka.da.infra.jms.messageprocessors.DomainResponseMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "kafkaDistributionAutomationInboundDomainResponsesMessageListener")
public class DistributionAutomationResponseMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationResponseMessageListener.class);

  @Autowired
  @Qualifier("kafkaDistributionAutomationInboundDomainResponsesMessageProcessor")
  private DomainResponseMessageProcessor domainResponseMessageProcessor;

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());

      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String correlationUid = objectMessage.getJMSCorrelationID();
      LOGGER.info("objectMessage CorrelationUID: {}", correlationUid);

      this.domainResponseMessageProcessor.processMessage(objectMessage);

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
