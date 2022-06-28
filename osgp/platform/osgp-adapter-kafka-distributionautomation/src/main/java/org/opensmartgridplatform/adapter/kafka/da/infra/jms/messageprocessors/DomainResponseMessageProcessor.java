/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.jms.messageprocessors;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out.StringMessageProducer;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class to process incoming domain responses. */
@Component(value = "kafkaDistributionAutomationInboundDomainResponsesMessageProcessor")
public class DomainResponseMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DomainResponseMessageProcessor.class);

  @Autowired private StringMessageProducer producer;

  @Override
  public void processMessage(final ObjectMessage message) {
    LOGGER.debug("Processing distribution automation response message");

    String correlationUid = null;
    MessageType messageType = null;
    String organisationIdentification = null;
    String deviceIdentification = null;
    String topic = null;

    final ResponseMessageResultType resultType;
    final String resultDescription;
    final ResponseMessage dataObject;

    try {
      correlationUid = message.getJMSCorrelationID();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
      topic = message.getStringProperty(Constants.TOPIC);

      messageType = MessageType.valueOf(message.getJMSType());

      resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
      resultDescription = message.getStringProperty(Constants.DESCRIPTION);

      dataObject = (ResponseMessage) message.getObject();
    } catch (final IllegalArgumentException e) {
      LOGGER.error("UNRECOVERABLE ERROR, received messageType {} is unknown.", messageType, e);
      logDebugInformation(
          messageType, correlationUid, organisationIdentification, deviceIdentification, topic);

      return;
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      logDebugInformation(
          messageType, correlationUid, organisationIdentification, deviceIdentification, topic);

      return;
    }

    try {
      final String fromTopic = StringUtils.isBlank(topic) ? "" : " from topic " + topic;
      LOGGER.info(
          "Handle message of type {} for device {}{} with result: {}, description {}.",
          messageType,
          deviceIdentification,
          fromTopic,
          resultType,
          resultDescription);
      this.handleMessage(messageType, dataObject);
    } catch (final RuntimeException e) {
      handleError(e, correlationUid);
    }
  }

  private void handleMessage(final MessageType messageType, final ResponseMessage message) {

    final Serializable dataObject = message.getDataObject();

    if (messageType == MessageType.GET_DATA) {
      final String textPayload;
      if (message.getDataObject() instanceof String) {
        textPayload = (String) message.getDataObject();
      } else {
        textPayload = new String((byte[]) message.getDataObject(), StandardCharsets.UTF_8);
      }
      this.producer.send(textPayload);
    } else {
      LOGGER.warn(
          "Discarding the message. For this component we only handle (MQTT) GET_DATA responses. "
              + "Received message type: {}, message {}",
          messageType,
          dataObject);
    }
  }

  private static void handleError(final RuntimeException e, final String correlationUid) {
    LOGGER.error(
        "Error '{}' occurred while trying to send message with correlationUid: {}",
        e.getMessage(),
        correlationUid,
        e);
  }

  private static void logDebugInformation(
      final MessageType messageType,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String topic) {
    LOGGER.debug("messageType: {}", messageType);
    LOGGER.debug("CorrelationUid: {}", correlationUid);
    LOGGER.debug("organisationIdentification: {}", organisationIdentification);
    LOGGER.debug("deviceIdentification: {}", deviceIdentification);
    LOGGER.debug("topic: {}", topic);
  }
}
