// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core;

import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "protocolDlmsOutboundOsgpCoreRequestsMessageSender")
public class OsgpRequestMessageSender {

  private final JmsTemplate jmsTemplate;

  public OsgpRequestMessageSender(
      @Qualifier("protocolDlmsOutboundOsgpCoreRequestsJmsTemplate") final JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final MessageMetadata messageMetadata) {

    log.info("Sending request message to GXF.");

    this.jmsTemplate.send(
        (final Session session) -> {
          final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
          objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
          objectMessage.setJMSType(messageType);
          objectMessage.setStringProperty(
              Constants.ORGANISATION_IDENTIFICATION,
              requestMessage.getOrganisationIdentification());
          objectMessage.setStringProperty(
              Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
          if (messageMetadata != null) {
            objectMessage.setJMSPriority(messageMetadata.getMessagePriority());
            objectMessage.setStringProperty(Constants.DOMAIN, messageMetadata.getDomain());
            objectMessage.setStringProperty(
                Constants.DOMAIN_VERSION, messageMetadata.getDomainVersion());
            objectMessage.setStringProperty(
                Constants.NETWORK_ADDRESS, messageMetadata.getNetworkAddress());
            objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, messageMetadata.isScheduled());
            objectMessage.setIntProperty(Constants.RETRY_COUNT, messageMetadata.getRetryCount());
            objectMessage.setBooleanProperty(
                Constants.BYPASS_RETRY, messageMetadata.isBypassRetry());
          }

          return objectMessage;
        });
  }
}
