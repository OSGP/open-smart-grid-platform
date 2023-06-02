//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor.DomainResponseMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "wsSmartMeteringInboundDomainResponsesMessageListener")
public class SmartMeteringResponseMessageListener implements MessageListener {

  @Autowired private DomainResponseMessageProcessor processor;

  public SmartMeteringResponseMessageListener() {
    // empty constructor
  }

  @Override
  public void onMessage(final Message message) {
    try {
      log.info("Received message of type: {}", message.getJMSType());

      final String messageType = message.getJMSType();
      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String correlationUid = objectMessage.getJMSCorrelationID();
      log.info("objectMessage CorrelationUID: {}", correlationUid);

      // Temporary if instead of message processor.
      if (messageType.equals(NotificationType.FIND_EVENTS.toString())) {
        // Save the events to the database.
        log.info("Saving events for FIND_EVENTS");
      }

      this.processor.processMessage(objectMessage);

      log.info("Finished processing CorrelationUID: {}", correlationUid);

    } catch (final JMSException ex) {
      log.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
