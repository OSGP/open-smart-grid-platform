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
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.SilentException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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

  @Autowired protected DeviceResponseMessageSender responseMessageSender;

  @Autowired
  @Qualifier("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
  protected MessageProcessorMap dlmsRequestMessageProcessorMap;

  @Autowired protected DomainHelperService domainHelperService;

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

    MessageMetadata messageMetadata = null;
    DlmsConnectionManager connectionManager = null;
    DlmsDevice device = null;

    try {
      messageMetadata = MessageMetadata.fromMessage(message);

      if (this.requiresExistingDevice()) {
        device = this.domainHelperService.findDlmsDevice(messageMetadata);
      }

      log.info(
          "{} called for device: {} for organisation: {}, correlationUID={}",
          message.getJMSType(),
          messageMetadata.getDeviceIdentification(),
          messageMetadata.getOrganisationIdentification(),
          messageMetadata.getCorrelationUid());

      if (this.usesDeviceConnection()) {
        connectionManager = this.createConnectionForDevice(device, messageMetadata);
      }

      final Serializable response =
          this.getResponse(connectionManager, device, message.getObject(), messageMetadata);
      this.sendResponse(messageMetadata, response);

    } catch (final JMSException exception) {
      this.logJmsException(log, exception, messageMetadata);
    } catch (final Exception exception) {

      this.sendErrorResponse(messageMetadata, exception, message.getObject());

    } finally {
      this.doConnectionPostProcessing(device, connectionManager);
    }
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
              "Unexpected exception during %s, correlationUID=%s",
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
