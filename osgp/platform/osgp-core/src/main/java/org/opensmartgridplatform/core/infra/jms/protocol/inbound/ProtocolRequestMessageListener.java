// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.inbound;

import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolRequestMessageListener implements MessageListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProtocolRequestMessageListener.class);

  private DomainRequestService domainRequestService;
  private List<DomainInfo> domainInfos;
  private MessageProcessorMap protocolRequestMessageProcessorMap;

  public ProtocolRequestMessageListener(
      final DomainRequestService domainRequestService,
      final List<DomainInfo> domainInfos,
      final MessageProcessorMap protocolRequestMessageProcessorMap) {
    this.domainRequestService = domainRequestService;
    this.domainInfos = domainInfos;
    this.protocolRequestMessageProcessorMap = protocolRequestMessageProcessorMap;
  }

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info("Received message of type: {}", message.getJMSType());
      final ObjectMessage objectMessage = (ObjectMessage) message;

      // Check if message can be processed by generic OSGP-CORE
      // message processor.
      try {
        final MessageProcessor processor =
            this.protocolRequestMessageProcessorMap.getMessageProcessor(objectMessage);

        processor.processMessage(objectMessage);

      } catch (final JMSException ex) {
        LOGGER.error("JMSException", ex);
        // The message needs to be sent to a domain adapter.
        this.sendMessageToDomainAdapter(
            (RequestMessage) objectMessage.getObject(), message.getJMSType());
      }

    } catch (final JMSException e) {
      LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
    }
  }

  /**
   * Send the RequestMessage to a domain adapter.
   *
   * @param requestMessage The RequestMessage to process.
   * @param messageType The MessageType of the RequestMessage to process.
   */
  private void sendMessageToDomainAdapter(
      final RequestMessage requestMessage, final String messageType) {

    String domain;
    final String domainVersion;
    if (DeviceFunction.PUSH_NOTIFICATION_ALARM.name().equals(messageType)) {
      domain = "SMART_METERING";
      domainVersion = "1.0";
    } else {
      domain = "CORE";
      domainVersion = "1.0";
    }
    DomainInfo domainInfo = null;

    for (final DomainInfo di : this.domainInfos) {
      if (domain.equals(di.getDomain()) && domainVersion.equals(di.getDomainVersion())) {
        domainInfo = di;
      }
    }

    if (domainInfo == null) {
      LOGGER.error(
          "No DomainInfo found, unable to send message of message type: {} to domain adapater. RequestMessage dropped.",
          messageType);
    } else {
      // Send message to domain adapter.
      this.domainRequestService.send(requestMessage, messageType, domainInfo);
    }
  }
}
