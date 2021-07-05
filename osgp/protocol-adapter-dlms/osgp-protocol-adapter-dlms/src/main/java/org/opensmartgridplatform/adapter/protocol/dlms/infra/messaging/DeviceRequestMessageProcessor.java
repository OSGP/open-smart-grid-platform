/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.SilentException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
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
public abstract class DeviceRequestMessageProcessor<S extends Serializable>
    extends DlmsConnectionMessageProcessor implements MessageProcessor {
  /** Constant to signal that message processor doesn't (have to) send a response. */
  protected static final String NO_RESPONSE = "NO-RESPONSE";

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessor.class);

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

  @SuppressWarnings("squid:S1193") // SilentException cannot be caught since
  // it does not extend Exception.
  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing {} request message", this.messageType);

    MessageMetadata messageMetadata = null;
    final DlmsConnectionManager conn = null;
    DlmsDevice device = null;

    try {
      messageMetadata = MessageMetadata.fromMessage(message);

      if (this.requiresExistingDevice()) {
        device = this.domainHelperService.findDlmsDevice(messageMetadata);
      }

      LOGGER.info(
          "{} called for device: {} for organisation: {}, correlationUID={}",
          message.getJMSType(),
          messageMetadata.getDeviceIdentification(),
          messageMetadata.getOrganisationIdentification(),
          messageMetadata.getCorrelationUid());

      final RequestWithMetadata<S> request =
          this.createRequestWithMetadata(message, messageMetadata);
      final Serializable response = this.getResponse(conn, device, request);
      this.sendResponse(messageMetadata, response);
    } catch (final JMSException exception) {
      this.logJmsException(LOGGER, exception, messageMetadata);
    } catch (final Exception exception) {
      this.sendErrorResponse(messageMetadata, exception, message.getObject());
    } finally {
      this.doConnectionPostProcessing(device, conn);
    }
  }

  protected Serializable getResponse(
      DlmsConnectionManager conn, final DlmsDevice device, final RequestWithMetadata<S> request)
      throws OsgpException {
    if (this.usesDeviceConnection()) {
      conn = this.createConnectionForDevice(device, request.getMetadata());
      return this.handleMessage(conn, device, request);
    } else {
      return this.handleMessage(device, request);
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
      LOGGER.error(errorMessage, exception);
    }
    this.sendResponseMessage(
        metadata,
        ResponseMessageResultType.NOT_OK,
        exception,
        this.responseMessageSender,
        requestObject);
  }

  protected RequestWithMetadata<S> createRequestWithMetadata(
      final ObjectMessage message, final MessageMetadata messageMetadata)
      throws JMSException, ProtocolAdapterException {
    try {
      return new RequestWithMetadata<>(messageMetadata, (S) message.getObject());
    } catch (final ClassCastException cce) {
      throw new ProtocolAdapterException("The request object has an incorrect type", cce);
    }
  }

  /**
   * Implementation of this method should call a service that can handle the requestObject and
   * return a response object to be put on the response queue. This response object can also be null
   * for methods that don't provide result data.
   *
   * @param conn the connection to the device.
   * @param device the device.
   * @param requestObject Request data object.
   * @return A serializable object to be put on the response queue.
   */
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final RequestWithMetadata<S> requestObject)
      throws OsgpException {
    throw new UnsupportedOperationException(
        "handleMessage(DlmsConnection, DlmsDevice, Serializable) should be overriden by a subclass, or "
            + "usesDeviceConnection should return false.");
  }

  protected Serializable handleMessage(
      final DlmsDevice device, final RequestWithMetadata<S> requestObject) throws OsgpException {
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
