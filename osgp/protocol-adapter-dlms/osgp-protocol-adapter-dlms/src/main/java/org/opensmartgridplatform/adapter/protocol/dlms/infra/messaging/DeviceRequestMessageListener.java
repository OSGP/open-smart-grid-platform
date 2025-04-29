// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@SuppressWarnings({"java:S1181", "java:S2139"})
@Component(value = "protocolDlmsInboundOsgpCoreRequestsMessageListener")
public class DeviceRequestMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageListener.class);

  private final MessageProcessorMap dlmsRequestMessageProcessorMap;

  public DeviceRequestMessageListener(
      @Qualifier("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
          final MessageProcessorMap dlmsRequestMessageProcessorMap) {
    this.dlmsRequestMessageProcessorMap = dlmsRequestMessageProcessorMap;
  }

  @Override
  public void onMessage(final Message message) {
    String jmsCorrelationID = "<unknown>";
    try {
      jmsCorrelationID = message.getJMSCorrelationID();

      LOGGER.info(
          "Received RequestMessage of type {} in DLMS Protocol Adapter with JMSCorrelationID: {}",
          message.getJMSType(),
          message.getJMSCorrelationID());

      final ObjectMessage objectMessage = (ObjectMessage) message;

      final MessageProcessor processor =
          this.dlmsRequestMessageProcessorMap.getMessageProcessor(objectMessage);

      if (processor != null) {
        processor.processMessage(objectMessage);
      } else {
        LOGGER.error(
            "MessageProcessor not found for message with JMSCorrelationID: {}", jmsCorrelationID);
      }

    } catch (final JMSException ex) {
      LOGGER.error(
          "Exception in dlms.DeviceRequestMessageListener. JMSCorrelationID: {} : {}",
          jmsCorrelationID,
          ex.getMessage(),
          ex);
    }
  }
}
