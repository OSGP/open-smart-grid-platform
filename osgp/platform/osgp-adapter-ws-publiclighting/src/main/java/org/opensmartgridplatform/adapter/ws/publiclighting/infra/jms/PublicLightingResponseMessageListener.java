// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "wsPublicLightingInboundDomainResponsesMessageListener")
public class PublicLightingResponseMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingResponseMessageListener.class);

  @Autowired
  @Qualifier("wsPublicLightingInboundDomainResponsesMessageProcessorMap")
  private MessageProcessorMap messageProcessorMap;

  @Override
  public void onMessage(final Message message) {
    try {
      final String messageType = message.getJMSType();
      LOGGER.info("Received message of type: {}", messageType);

      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String correlationUid = objectMessage.getJMSCorrelationID();
      LOGGER.info("objectMessage CorrelationUID: {}", correlationUid);

      final MessageProcessor processor =
          this.messageProcessorMap.getMessageProcessor(objectMessage);

      processor.processMessage(objectMessage);

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
