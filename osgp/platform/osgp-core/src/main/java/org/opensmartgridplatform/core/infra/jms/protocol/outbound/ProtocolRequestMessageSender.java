/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolRequestService;
import org.opensmartgridplatform.core.infra.messaging.CoreLogItemRequestMessage;
import org.opensmartgridplatform.core.infra.messaging.CoreLogItemRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * This class sends protocol request messages to the requests queue for the specific version of the
 * protocol
 */
public class ProtocolRequestMessageSender implements ProtocolRequestService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageSender.class);

  @Autowired private int messageGroupCacheSize;

  @Autowired private CoreLogItemRequestMessageSender coreLogItemRequestMessageSender;

  @Autowired
  private ProtocolRequestMessageJmsTemplateFactory protocolRequestMessageJmsTemplateFactory;

  @Override
  public boolean isSupported(final ProtocolInfo protocolInfo) {
    return this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo) != null;
  }

  @Override
  public void send(final ProtocolRequestMessage message, final ProtocolInfo protocolInfo) {

    LOGGER.info(
        "Sending protocol request message for device [{}] using protocol [{}] with version [{}]",
        message.getDeviceIdentification(),
        protocolInfo.getProtocol(),
        protocolInfo.getProtocolVersion());

    final JmsTemplate jmsTemplate =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo);

    LOGGER.info(
        "Message sender destination queue: [{}] for protocol [{}] with version [{}]",
        jmsTemplate.getDefaultDestination(),
        protocolInfo.getProtocol(),
        protocolInfo.getProtocolVersion());

    this.sendMessage(message, protocolInfo, jmsTemplate);
  }

  private void sendMessage(
      final ProtocolRequestMessage requestMessage,
      final ProtocolInfo protocolInfo,
      final JmsTemplate jmsTemplate) {
    LOGGER.info("Sending request message to protocol requests queue");

    jmsTemplate.send(session -> this.createObjectMessage(requestMessage, protocolInfo, session));

    if (requestMessage.getRetryCount() != 0) {
      final String decodedMessageWithDescription =
          String.format(
              "retry count= %s, correlationuid= %s ",
              requestMessage.getRetryCount(), requestMessage.getCorrelationUid());

      final CoreLogItemRequestMessage coreLogItemRequestMessage =
          new CoreLogItemRequestMessage(
              requestMessage.getDeviceIdentification(),
              requestMessage.getOrganisationIdentification(),
              decodedMessageWithDescription);

      this.coreLogItemRequestMessageSender.send(coreLogItemRequestMessage);
    }
  }

  private ObjectMessage createObjectMessage(
      final ProtocolRequestMessage requestMessage,
      final ProtocolInfo protocolInfo,
      final Session session)
      throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage(requestMessage.getRequest());
    objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
    objectMessage.setJMSType(requestMessage.getMessageType());
    objectMessage.setJMSPriority(requestMessage.getMessagePriority());
    objectMessage.setStringProperty(Constants.DOMAIN, requestMessage.getDomain());
    objectMessage.setStringProperty(Constants.DOMAIN_VERSION, requestMessage.getDomainVersion());
    objectMessage.setStringProperty(
        Constants.ORGANISATION_IDENTIFICATION, requestMessage.getOrganisationIdentification());

    final String deviceIdentification = requestMessage.getDeviceIdentification();
    objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION, deviceIdentification);
    objectMessage.setStringProperty(Constants.IP_ADDRESS, requestMessage.getIpAddress());
    objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, requestMessage.isScheduled());
    objectMessage.setIntProperty(Constants.RETRY_COUNT, requestMessage.getRetryCount());
    objectMessage.setBooleanProperty(Constants.BYPASS_RETRY, requestMessage.bypassRetry());

    if (!protocolInfo.isParallelRequestsAllowed()) {
      final String messageGroupId =
          ProtocolRequestMessageSender.this.getMessageGroupId(deviceIdentification);
      LOGGER.debug(
          "Setting message group property for device {} to: {}",
          deviceIdentification,
          messageGroupId);
      objectMessage.setStringProperty(Constants.MESSAGE_GROUP, messageGroupId);
    }
    return objectMessage;
  }

  protected String getMessageGroupId(final String deviceIdentification) {
    return String.valueOf(Math.abs(deviceIdentification.hashCode() % this.messageGroupCacheSize));
  }
}
