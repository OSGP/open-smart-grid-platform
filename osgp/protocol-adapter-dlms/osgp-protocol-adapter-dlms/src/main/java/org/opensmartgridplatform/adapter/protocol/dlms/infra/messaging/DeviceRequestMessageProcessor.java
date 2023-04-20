/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.SilentException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ThrowingConsumer;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.throttling.ThrottlingPermitDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
@Slf4j
public abstract class DeviceRequestMessageProcessor extends DlmsConnectionMessageProcessor
    implements MessageProcessor {

  /** Constant to signal that message processor doesn't (have to) send a response. */
  protected static final String NO_RESPONSE = "NO-RESPONSE";

  @Value("#{T(java.time.Duration).parse('${device.key.processing.timeout:PT5M}')}")
  private Duration deviceKeyProcessingTimeout;

  @Autowired protected DeviceResponseMessageSender responseMessageSender;

  @Autowired
  @Qualifier("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
  protected MessageProcessorMap dlmsRequestMessageProcessorMap;

  @Autowired protected DomainHelperService domainHelperService;

  @Autowired private DeviceRequestMessageSender deviceRequestMessageSender;

  protected final MessageType messageType;

  /**
   * Each MessageProcessor should register it's MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected DeviceRequestMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.dlmsRequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    log.debug("Processing {} request message", this.messageType);

    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    final Serializable messageObject = message.getObject();

    try {
      final DlmsDevice device;
      if (this.requiresExistingDevice()) {
        device = this.domainHelperService.findDlmsDevice(messageMetadata);
      } else {
        device = null;
      }
      if (this.usesDeviceConnection()) {
        /*
         * Set up a consumer to be called back with a DlmsConnectionManager for which the connection
         * with the device has been created. Note that when usesDeviceConnection is true, in this
         * way all logic in processMessageTasks is executed only after the connection to the device
         * has successfully been established.
         */
        final ThrowingConsumer<DlmsConnectionManager> taskForConnectionManager =
            connectionManager ->
                this.processMessageTasks(messageObject, messageMetadata, connectionManager, device);
        this.createAndHandleConnectionForDevice(device, messageMetadata, taskForConnectionManager);
      } else {
        this.processMessageTasks(messageObject, messageMetadata, null, device);
      }
    } catch (final ThrottlingPermitDeniedException exception) {

      /*
       * Throttling permit for network access not granted, send the request back to the queue to be
       * picked up again a little later by the message listener for device requests.
       */
      log.info(
          "Throttling permit was denied for deviceIdentification {} for network segment ({}, {}) for {}. retry message in {} ms",
          messageMetadata.getDeviceIdentification(),
          exception.getBaseTransceiverStationId(),
          exception.getCellId(),
          exception.getConfigurationName(),
          this.throttlingClientConfig.permitRejectedDelay().toMillis());
      this.deviceRequestMessageSender.send(
          messageObject, messageMetadata, this.throttlingClientConfig.permitRejectedDelay());

    } catch (final DeviceKeyProcessAlreadyRunningException exception) {

      this.deviceRequestMessageSender.send(
          messageObject, messageMetadata, this.deviceKeyProcessingTimeout);
    } catch (final Exception exception) {
      this.sendErrorResponse(messageMetadata, exception, messageObject);
    }
  }

  public void processMessageTasks(
      final Serializable messageObject,
      final MessageMetadata messageMetadata,
      final DlmsConnectionManager connectionManager,
      final DlmsDevice device)
      throws OsgpException {
    try {
      if (this.maxScheduleTimeExceeded(messageMetadata)) {
        log.info(
            "Processing message of type {} for correlation UID {} exceeded max schedule time: {} ({})",
            messageMetadata.getMessageType(),
            messageMetadata.getCorrelationUid(),
            messageMetadata.getMaxScheduleTime(),
            Instant.ofEpochMilli(messageMetadata.getMaxScheduleTime()));
        this.sendErrorResponse(
            messageMetadata,
            new FunctionalException(
                FunctionalExceptionType.MAX_SCHEDULE_TIME_EXCEEDED, ComponentType.PROTOCOL_DLMS),
            messageObject);
        return;
      }

      log.info(
          "{} called for device: {} for organisation: {}, correlationUID: {}",
          messageMetadata.getMessageType(),
          messageMetadata.getDeviceIdentification(),
          messageMetadata.getOrganisationIdentification(),
          messageMetadata.getCorrelationUid());

      final Serializable response =
          this.getResponse(connectionManager, device, messageObject, messageMetadata);
      this.sendResponse(messageMetadata, response);

    } finally {
      this.doConnectionPostProcessing(device, connectionManager, messageMetadata);
    }
  }

  private boolean maxScheduleTimeExceeded(final MessageMetadata messageMetadata) {
    final Long maxScheduleTime = messageMetadata.getMaxScheduleTime();
    return maxScheduleTime != null && System.currentTimeMillis() > maxScheduleTime;
  }

  protected Serializable getResponse(
      final DlmsConnectionManager connectionManager,
      final DlmsDevice device,
      final Serializable request,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    if (connectionManager != null) {
      return this.handleMessage(connectionManager, device, request, messageMetadata);
    } else {
      return this.handleMessage(device, request, messageMetadata);
    }
  }

  protected void sendResponse(final MessageMetadata metadata, final Serializable response) {
    if (!NO_RESPONSE.equals(response)) {
      this.sendResponseMessage(
          metadata, ResponseMessageResultType.OK, null, this.responseMessageSender, response);
    }
  }

  protected void sendErrorResponse(
      final MessageMetadata metadata, final Exception exception, final Serializable requestObject) {
    // Return original request + exception
    if (!(exception instanceof SilentException)) {
      final String errorMessage =
          String.format(
              "Unexpected exception during %s, correlationUID: %s",
              this.messageType.name(), metadata.getCorrelationUid());
      log.error(errorMessage, exception);
    }
    this.sendResponseMessage(
        metadata,
        ResponseMessageResultType.NOT_OK,
        exception,
        this.responseMessageSender,
        requestObject);
  }

  /**
   * Implementation of this method should call a service that can handle the requestObject and
   * return a response object to be put on the response queue. This response object can also be null
   * for methods that don't provide result data.
   *
   * @param conn the connection to the device.
   * @param device the device.
   * @param requestObject Request data object.
   * @param messageMetadata the metadata of the request message
   * @return A serializable object to be put on the response queue.
   */
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    throw new UnsupportedOperationException(
        "handleMessage(DlmsConnection, DlmsDevice, Serializable) should be overriden by a subclass, or "
            + "usesDeviceConnection should return false.");
  }

  protected Serializable handleMessage(
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    throw new UnsupportedOperationException(
        "handleMessage(Serializable) should be overriden by a subclass, or usesDeviceConnection should return"
            + " true.");
  }

  /**
   * Used to determine if the handleMessage needs a device connection or not. Default value is true,
   * override to alter behaviour of subclasses.
   *
   * @return Use device connection in handleMessage.
   */
  protected boolean usesDeviceConnection() {
    return true;
  }

  protected boolean requiresExistingDevice() {
    return true;
  }
}
