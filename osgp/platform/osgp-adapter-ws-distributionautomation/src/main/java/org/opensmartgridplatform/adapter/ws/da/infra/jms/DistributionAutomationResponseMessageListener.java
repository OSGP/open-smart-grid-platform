//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.da.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.ws.da.infra.jms.messageprocessors.DomainResponseMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "wsDistributionAutomationInboundDomainResponsesMessageListener")
public class DistributionAutomationResponseMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationResponseMessageListener.class);

  @Autowired
  @Qualifier("wsDistributionAutomationInboundDomainResponsesMessageProcessor")
  private DomainResponseMessageProcessor domainResponseMessageProcessor;

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());

      final String correlationUid = message.getJMSCorrelationID();
      LOGGER.info("objectMessage CorrelationUID: {}", correlationUid);

      this.domainResponseMessageProcessor.processMessage((ObjectMessage) message);

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
