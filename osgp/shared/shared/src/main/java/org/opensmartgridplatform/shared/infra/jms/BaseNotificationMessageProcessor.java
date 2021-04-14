/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class BaseNotificationMessageProcessor implements MessageProcessor {

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(BaseNotificationMessageProcessor.class);
  /**
   * This is the message sender needed for the message processor implementation to handle an error.
   */
  protected NotificationResponseMessageSender responseMessageSender;

  /** The map of message processor instances. */
  protected final MessageProcessorMap messageProcessorMap;

  private final ComponentType componentType;

  /** The message types that a message processor implementation can handle. */
  protected final List<MessageType> messageTypes = new ArrayList<>();

  /**
   * Construct a message processor instance by passing in the message type.
   *
   * @param messageType The message type a message processor can handle.
   */
  protected BaseNotificationMessageProcessor(
      final NotificationResponseMessageSender responseMessageSender,
      final MessageProcessorMap messageProcessorMap,
      final MessageType messageType) {
    this.responseMessageSender = responseMessageSender;
    this.messageProcessorMap = messageProcessorMap;
    this.messageTypes.add(messageType);
    this.componentType = ComponentType.UNKNOWN;
  }

  /**
   * In case a message processor instance can process multiple message types, a message type can be
   * added.
   *
   * @param messageType The message type a message processor can handle.
   */
  protected void addMessageType(final MessageType messageType) {
    this.messageTypes.add(messageType);
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    for (final MessageType messageType : this.messageTypes) {
      this.messageProcessorMap.addMessageProcessor(messageType, this);
    }
  }

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * @param e The exception.
   * @param correlationUid The correlation UID.
   * @param organisationIdentification The organisation identification.
   * @param deviceIdentification The device identification.
   * @param messageType The message type.
   */
  protected void handleError(
      final Exception e,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String messageType) {
    LOGGER.info("handeling error: {} for message type: {}", e.getMessage(), messageType);
    final OsgpException osgpException = osgpExceptionOf(e);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .withDataObject(e)
            .build();
    this.responseMessageSender.send(responseMessage, messageType);
  }

  private OsgpException osgpExceptionOf(Exception e) {
    if (e instanceof OsgpException) {
      return (OsgpException) e;
    }
    return new TechnicalException(componentType, "An unknown error occurred", e);
  }
}
