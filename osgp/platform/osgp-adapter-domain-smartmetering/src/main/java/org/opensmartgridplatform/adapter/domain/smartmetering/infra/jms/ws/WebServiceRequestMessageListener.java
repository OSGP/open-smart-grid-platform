//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws;

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

// Fetch incoming messages from the requests queue of web service adapter.
@Component(value = "domainSmartMeteringInboundWebServiceRequestsMessageListener")
public class WebServiceRequestMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WebServiceRequestMessageListener.class);

  @Autowired
  @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
  private MessageProcessorMap messageProcessorMap;

  public WebServiceRequestMessageListener() {
    // empty constructor
  }

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());

      final ObjectMessage objectMessage = (ObjectMessage) message;

      final MessageProcessor processor =
          this.messageProcessorMap.getMessageProcessor(objectMessage);

      processor.processMessage(objectMessage);

    } catch (final JMSException ex) {
      LOGGER.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
