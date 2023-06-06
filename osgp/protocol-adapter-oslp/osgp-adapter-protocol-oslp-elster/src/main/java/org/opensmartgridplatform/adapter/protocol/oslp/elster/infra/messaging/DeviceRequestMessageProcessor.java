// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.DeviceService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.services.DeviceResponseService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DeviceRequestMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessor.class);

  @Autowired protected DeviceService deviceService;

  @Autowired protected DeviceResponseMessageSender responseMessageSender;

  @Autowired protected DeviceResponseService deviceResponseService;

  @Autowired
  @Qualifier("protocolOslpDeviceRequestMessageProcessorMap")
  protected MessageProcessorMap oslpRequestMessageProcessorMap;

  protected final MessageType messageType;

  protected static final String UNEXPECTED_EXCEPTION = "An unknown error occurred";

  /**
   * Each MessageProcessor should register it's MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected DeviceRequestMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  protected void printDomainInfo(
      final String messageType, final String domain, final String domainVersion) {
    LOGGER.info(
        "Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.oslpRequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  protected void handleEmptyDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final String domain,
      final String domainVersion,
      final String messageType,
      final int retryCount) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    TechnicalException ex = null;

    try {
      final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
      this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
    } catch (final TechnicalException e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      ex = e;
    }

    final MessageMetadata messageMetadata =
        MessageMetadataFactory.from(deviceResponse, messageType)
            .builder()
            .withDomain(domain)
            .withDomainVersion(domainVersion)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(ex)
            .build();

    responseMessageSender.send(responseMessage);
  }

  protected void handleScheduledEmptyDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final String domain,
      final String domainVersion,
      final String messageType,
      final boolean isScheduled,
      final int retryCount) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    TechnicalException ex = null;

    try {
      final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
      this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
    } catch (final TechnicalException e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      ex = e;
    }

    final MessageMetadata messageMetadata =
        MessageMetadataFactory.from(deviceResponse, messageType)
            .builder()
            .withDomain(domain)
            .withDomainVersion(domainVersion)
            .withScheduled(isScheduled)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(ex)
            .build();

    responseMessageSender.send(responseMessage);
  }

  protected void handleError(
      final IOException e,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String domain,
      final String domainVersion,
      final String messageType,
      final int messagePriority,
      final int retryCount) {
    LOGGER.error("Error while processing message", e);
    // Set the exception to a class known by all OSGP components
    final TechnicalException ex =
        new TechnicalException(ComponentType.PROTOCOL_OSLP, UNEXPECTED_EXCEPTION);

    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(messageType)
            .withDomain(domain)
            .withDomainVersion(domainVersion)
            .withMessagePriority(messagePriority)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage protocolResponseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(ResponseMessageResultType.NOT_OK)
            .osgpException(ex)
            .build();

    this.responseMessageSender.send(protocolResponseMessage);
  }

  protected void handleFunctionalException(
      final FunctionalException e, final MessageMetadata messageMetadata) {
    LOGGER.error(
        "Functional error while processing message, using MessageMetadata to create error response",
        e);

    final ProtocolResponseMessage protocolResponseMessage =
        this.createProtocolResponseMessage(messageMetadata, e);
    this.responseMessageSender.send(protocolResponseMessage);
  }

  protected void handleError(final RuntimeException e, final MessageMetadata messageMetadata) {
    LOGGER.error(
        "Error while processing message, using MessageMetadata to create error response", e);

    // Set the exception to a class known by all OSGP components
    final TechnicalException ex =
        new TechnicalException(ComponentType.PROTOCOL_OSLP, UNEXPECTED_EXCEPTION);
    final ProtocolResponseMessage protocolResponseMessage =
        this.createProtocolResponseMessage(messageMetadata, ex);
    this.responseMessageSender.send(protocolResponseMessage);
  }

  private ProtocolResponseMessage createProtocolResponseMessage(
      final MessageMetadata messageMetadata, final OsgpException ex) {
    return ProtocolResponseMessage.newBuilder()
        .messageMetadata(messageMetadata)
        .result(ResponseMessageResultType.NOT_OK)
        .osgpException(ex)
        .build();
  }

  public void handleUnableToConnectDeviceResponse(
      final DeviceResponse deviceResponse,
      final Throwable t,
      final String domain,
      final String domainVersion,
      final String messageType,
      final boolean isScheduled,
      final int retryCount) {

    LOGGER.error("Error while connecting to or communicating with device", t);

    final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
    // Set the exception to a class known by all OSGP components
    final TechnicalException ex =
        new TechnicalException(
            ComponentType.PROTOCOL_OSLP,
            StringUtils.isBlank(t.getMessage()) ? UNEXPECTED_EXCEPTION : t.getMessage());

    final MessageMetadata messageMetadata =
        MessageMetadataFactory.from(deviceResponse, messageType)
            .builder()
            .withDomain(domain)
            .withDomainVersion(domainVersion)
            .withScheduled(isScheduled)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(ex)
            .build();

    this.responseMessageSender.send(responseMessage);
  }
}
