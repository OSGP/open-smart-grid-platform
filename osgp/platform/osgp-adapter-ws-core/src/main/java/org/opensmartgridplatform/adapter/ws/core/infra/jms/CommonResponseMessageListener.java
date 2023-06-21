// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.messageprocessor.DomainResponseMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "wsCoreInboundDomainResponsesMessageListener")
public class CommonResponseMessageListener implements MessageListener {

  @Autowired private DomainResponseMessageProcessor processor;

  public CommonResponseMessageListener() {
    // empty constructor
  }

  @Override
  public void onMessage(final Message message) {
    try {
      final ObjectMessage objectMessage = (ObjectMessage) message;
      final String correlationUid = objectMessage.getJMSCorrelationID();
      log.info(
          "Received message of type: {}, CorrelationUID: {}", message.getJMSType(), correlationUid);

      this.processor.processMessage(objectMessage);

      log.info("Finished processing CorrelationUID: {}", correlationUid);

    } catch (final JMSException ex) {
      log.error("Exception: {} ", ex.getMessage(), ex);
    }
  }
}
