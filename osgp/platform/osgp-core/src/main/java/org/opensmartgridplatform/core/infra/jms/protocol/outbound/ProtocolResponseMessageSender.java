// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import javax.jms.Destination;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolResponseService;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

public class ProtocolResponseMessageSender implements ProtocolResponseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolResponseMessageSender.class);

  @Autowired private ProtocolResponseMessageJmsTemplateFactory factory;

  @Override
  public void send(
      final ResponseMessage responseMessage,
      final String messageType,
      final ProtocolInfo protocolInfo,
      final MessageMetadata messageMetadata) {

    final String key = protocolInfo.getKey();

    final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

    this.sendWithDestination(
        responseMessage,
        messageType,
        protocolInfo,
        messageMetadata,
        jmsTemplate.getDefaultDestination());
  }

  @Override
  public void sendWithDestination(
      final ResponseMessage responseMessage,
      final String messageType,
      final ProtocolInfo protocolInfo,
      final MessageMetadata messageMetadata,
      final Destination destination) {

    final String key = protocolInfo.getKey();

    final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

    this.sendWithDestination(
        responseMessage, messageType, jmsTemplate, messageMetadata, destination);
  }

  private void sendWithDestination(
      final ResponseMessage responseMessage,
      final String messageType,
      final JmsTemplate jmsTemplate,
      final MessageMetadata messageMetadata,
      final Destination destination) {
    LOGGER.info("Sending response message to protocol responses incoming queue");

    jmsTemplate.send(
        destination,
        session -> {
          final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
          objectMessage.setJMSCorrelationID(messageMetadata.getCorrelationUid());
          objectMessage.setJMSType(messageType);
          objectMessage.setStringProperty(
              Constants.ORGANISATION_IDENTIFICATION,
              messageMetadata.getOrganisationIdentification());
          objectMessage.setStringProperty(
              Constants.DEVICE_IDENTIFICATION, messageMetadata.getDeviceIdentification());
          objectMessage.setStringProperty(Constants.DOMAIN, messageMetadata.getDomain());
          objectMessage.setStringProperty(
              Constants.DOMAIN_VERSION, messageMetadata.getDomainVersion());
          objectMessage.setStringProperty(
              Constants.NETWORK_ADDRESS, messageMetadata.getIpAddress());
          objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, messageMetadata.isScheduled());
          objectMessage.setIntProperty(Constants.RETRY_COUNT, messageMetadata.getRetryCount());
          objectMessage.setBooleanProperty(Constants.BYPASS_RETRY, messageMetadata.isBypassRetry());
          return objectMessage;
        });
  }
}
