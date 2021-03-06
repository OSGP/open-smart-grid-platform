/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.messaging;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component(value = "protocolMqttOutboundOsgpCoreRequestsMessageSender")
public class OutboundOsgpCoreRequestMessageSender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundOsgpCoreRequestMessageSender.class);

  private final JmsTemplate protocolMqttOutboundOsgpCoreRequestsJmsTemplate;

  public OutboundOsgpCoreRequestMessageSender(
      final JmsTemplate protocolMqttOutboundOsgpCoreRequestsJmsTemplate) {
    this.protocolMqttOutboundOsgpCoreRequestsJmsTemplate =
        protocolMqttOutboundOsgpCoreRequestsJmsTemplate;
  }

  public void send(
      final RequestMessage requestMessage,
      final String messageType,
      final MessageMetadata messageMetadata) {
    LOGGER.info("Sending request message to OSGP.");
    this.protocolMqttOutboundOsgpCoreRequestsJmsTemplate.send(
        (final Session session) -> {
          final ObjectMessage objectMessage =
              this.createObjectMessage(session, requestMessage, messageType);
          if (messageMetadata != null) {
            this.addMessageMetadata(objectMessage, messageMetadata);
          }
          return objectMessage;
        });
  }

  private ObjectMessage createObjectMessage(
      final Session session, final RequestMessage requestMessage, final String messageType)
      throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
    objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
    objectMessage.setJMSType(messageType);
    objectMessage.setStringProperty(
        Constants.ORGANISATION_IDENTIFICATION, requestMessage.getOrganisationIdentification());
    objectMessage.setStringProperty(
        Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
    return objectMessage;
  }

  private void addMessageMetadata(
      final ObjectMessage objectMessage, final MessageMetadata messageMetadata)
      throws JMSException {
    objectMessage.setStringProperty(Constants.DOMAIN, messageMetadata.getDomain());
    objectMessage.setStringProperty(Constants.DOMAIN_VERSION, messageMetadata.getDomainVersion());
    objectMessage.setStringProperty(Constants.IP_ADDRESS, messageMetadata.getIpAddress());
    objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, messageMetadata.isScheduled());
    objectMessage.setIntProperty(Constants.RETRY_COUNT, messageMetadata.getRetryCount());
    objectMessage.setBooleanProperty(Constants.BYPASS_RETRY, messageMetadata.isBypassRetry());
  }
}
