/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.SilentException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ThrowingConsumer;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsConnectionMessageProcessor;
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
public abstract class OsgpResponseMessageProcessor extends DlmsConnectionMessageProcessor
    implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponseMessageProcessor.class);

  @Autowired
  @Qualifier("protocolDlmsOutboundOsgpCoreResponsesMessageSender")
  protected DeviceResponseMessageSender responseMessageSender;

  @Autowired
  @Qualifier("protocolDlmsInboundOsgpResponsesMessageProcessorMap")
  protected MessageProcessorMap messageProcessorMap;

  @Autowired protected DomainHelperService domainHelperService;

  protected final MessageType messageType;

  /**
   * Each MessageProcessor should register it's MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected OsgpResponseMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.messageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing {} request message", this.messageType);

    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);

    final ThrowingConsumer<DlmsConnectionManager> taskForConnectionManager =
        conn -> this.processMessageTask(message, messageMetadata, conn);

    if (this.usesDeviceConnection()) {
      try {
        this.createConnectionForDevice(
            this.domainHelperService.findDlmsDevice(messageMetadata),
            messageMetadata,
            taskForConnectionManager);
      } catch (final OsgpException e) {
        LOGGER.error("Something went wrong with the DlmsConnection", e);
      }
    }
  }

  @SuppressWarnings("squid:S1193") // SilentException cannot be caught since
  // it does not extend Exception.
  private void processMessageTask(
      final ObjectMessage message,
      final MessageMetadata messageMetadata,
      final DlmsConnectionManager conn)
      throws JMSException, OsgpException {
    try {
      final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

      LOGGER.info(
          "{} called for device: {} for organisation: {}",
          message.getJMSType(),
          messageMetadata.getDeviceIdentification(),
          messageMetadata.getOrganisationIdentification());

      if (this.usesDeviceConnection()) {
        this.handleMessage(
            conn, this.domainHelperService.findDlmsDevice(messageMetadata), message.getObject());
      } else {
        this.handleMessage(device, message);
      }
    } catch (final JMSException exception) {
      this.logJmsException(LOGGER, exception, messageMetadata);
    } catch (final Exception exception) {
      // Return original request + exception
      if (!(exception instanceof SilentException)) {
        LOGGER.error("Unexpected exception during {}", this.messageType.name(), exception);
      }

      this.sendResponseMessage(
          messageMetadata,
          ResponseMessageResultType.NOT_OK,
          exception,
          this.responseMessageSender,
          message.getObject());
    } finally {
      final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
      this.doConnectionPostProcessing(device, conn, messageMetadata);
    }
  }

  protected boolean getBooleanPropertyValue(final Message message, final String propertyName)
      throws JMSException {
    return message.propertyExists(propertyName) && message.getBooleanProperty(propertyName);
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
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable requestObject)
      throws OsgpException {
    throw new UnsupportedOperationException(
        "handleMessage(DlmsConnection, DlmsDevice, Serializable) should be overriden by a subclass, or usesDeviceConnection should return false.");
  }

  protected Serializable handleMessage(final DlmsDevice device, final ObjectMessage message)
      throws OsgpException {
    throw new UnsupportedOperationException(
        "handleMessage(Serializable) should be overriden by a subclass, or usesDeviceConnection should return true.");
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
}
