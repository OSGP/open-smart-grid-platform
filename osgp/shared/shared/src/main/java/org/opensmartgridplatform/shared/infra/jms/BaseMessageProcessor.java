// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
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
public abstract class BaseMessageProcessor implements MessageProcessor {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageProcessor.class);

  /**
   * This is the message sender needed for the message processor implementation to forward response
   * messages to web service adapter.
   */
  protected final ResponseMessageSender responseMessageSender;

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
  protected BaseMessageProcessor(
      final ResponseMessageSender responseMessageSender,
      final MessageProcessorMap messageProcessorMap,
      final MessageType messageType,
      final ComponentType componentType) {
    this.responseMessageSender = responseMessageSender;
    this.messageProcessorMap = messageProcessorMap;
    this.messageTypes.add(messageType);
    this.componentType = componentType;
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
   * @param messagePriority The priority of the message.
   */
  protected void handleError(
      final Exception e,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String messageType,
      final int messagePriority) {
    LOGGER.info("handling error: {} for message type: {}", e.getMessage(), messageType);
    final OsgpException osgpException = this.osgpExceptionOf(e);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .withDataObject(e)
            .withMessagePriority(messagePriority)
            .withMessageType(messageType)
            .build();
    this.responseMessageSender.send(responseMessage);
  }

  private OsgpException osgpExceptionOf(final Exception e) {
    if (e instanceof OsgpException) {
      return (OsgpException) e;
    }
    return new TechnicalException(this.componentType, "An unknown error occurred", e);
  }
}
