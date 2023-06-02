//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.messageprocessors;

import java.time.Duration;
import java.time.Instant;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Class for processing public lighting connect response messages */
@Component("domainPublicLightingConnectResponseMessageProcessor")
public class PublicLightingConnectResponseMessageProcessor extends BaseMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingConnectResponseMessageProcessor.class);

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Value(
      "#{T(java.time.Duration).parse('${communication.monitoring.minimum.duration.between.communication.time.updates:PT1M}')}")
  private Duration minimumDurationBetweenCommunicationTimeUpdates;

  @Autowired
  protected PublicLightingConnectResponseMessageProcessor(
      final ResponseMessageSender webServiceResponseMessageSender,
      @Qualifier("domainPublicLightingInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap osgpCoreResponseMessageProcessorMap) {
    super(
        webServiceResponseMessageSender,
        osgpCoreResponseMessageProcessorMap,
        MessageType.CONNECT,
        ComponentType.DOMAIN_PUBLIC_LIGHTING);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing public lighting set transition response message");

    String correlationUid = null;
    String messageType = null;
    int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    String organisationIdentification = null;
    String deviceIdentification = null;

    ResponseMessage responseMessage;
    ResponseMessageResultType responseMessageResultType = null;
    OsgpException osgpException = null;

    try {
      correlationUid = message.getJMSCorrelationID();
      messageType = message.getJMSType();
      messagePriority = message.getJMSPriority();
      organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
      deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

      responseMessage = (ResponseMessage) message.getObject();
      responseMessageResultType = responseMessage.getResult();
      osgpException = responseMessage.getOsgpException();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("correlationUid: {}", correlationUid);
      LOGGER.debug("messageType: {}", messageType);
      LOGGER.debug("messagePriority: {}", messagePriority);
      LOGGER.debug("organisationIdentification: {}", organisationIdentification);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
      LOGGER.debug("deviceIdentification: {}", deviceIdentification);
      LOGGER.debug("osgpException", osgpException);
      return;
    }

    try {
      LOGGER.info("Received message of type: {}", messageType);

      switch (responseMessageResultType) {
        case OK:
          final RtuDevice rtu =
              this.rtuDeviceRepository
                  .findByDeviceIdentification(deviceIdentification)
                  .orElse(null);
          if (rtu != null && this.shouldUpdateCommunicationTime(rtu)) {
            rtu.messageReceived();
            this.rtuDeviceRepository.save(rtu);
          } else {
            LOGGER.info("No RTU found with device identification {}", deviceIdentification);
          }
          break;
        case NOT_FOUND:
          // Should never happen
          LOGGER.warn(
              "Received result not found while connecting to device {}", deviceIdentification);
          break;
        case NOT_OK:
          LOGGER.error(
              "Received result NOT OK while trying to connect to device {}",
              deviceIdentification,
              osgpException);
      }

    } catch (final Exception e) {
      this.handleError(
          e,
          correlationUid,
          organisationIdentification,
          deviceIdentification,
          messageType,
          messagePriority);
    }
  }

  private boolean shouldUpdateCommunicationTime(final RtuDevice device) {
    final Instant timeToCheck =
        Instant.now().minus(this.minimumDurationBetweenCommunicationTimeUpdates);
    final Instant timeOfLastCommunication = device.getLastCommunicationTime();
    return timeOfLastCommunication.isBefore(timeToCheck);
  }
}
