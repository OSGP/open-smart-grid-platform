/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class OsgpCoreResponseMessageProcessor implements MessageProcessor {

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OsgpCoreResponseMessageProcessor.class);

  /**
   * This is the message sender needed for the message processor implementation to forward response
   * messages to web service adapter.
   */
  private final WebServiceResponseMessageSender webServiceResponseMessageSender;

  /** The map of message processor instances. */
  private final MessageProcessorMap osgpCoreResponseMessageProcessorMap;

  private final ComponentType componentType;

  /** The message types that a message processor implementation can handle. */
  private final List<MessageType> messageTypes = new ArrayList<>();

  /**
   * Construct a message processor instance by passing in the message type.
   *
   * @param messageType The message type a message processor can handle.
   * @param componentType the OSGP component handling the message
   */
  protected OsgpCoreResponseMessageProcessor(
      final WebServiceResponseMessageSender webServiceResponseMessageSender,
      final MessageProcessorMap osgpCoreResponseMessageProcessorMap,
      final MessageType messageType,
      final ComponentType componentType) {
    this.webServiceResponseMessageSender = webServiceResponseMessageSender;
    this.osgpCoreResponseMessageProcessorMap = osgpCoreResponseMessageProcessorMap;
    this.componentType = componentType;
    this.messageTypes.add(messageType);
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
      this.osgpCoreResponseMessageProcessorMap.addMessageProcessor(messageType, this);
    }
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing response message");

    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);

    final ResponseMessage responseMessage;
    OsgpException osgpException = null;

    try {
      responseMessage = (ResponseMessage) message.getObject();
      osgpException = responseMessage.getOsgpException();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug(messageMetadata.toString());
      LOGGER.debug("osgpException", osgpException);
      return;
    }

    try {

      if (osgpException != null) {
        this.handleError(osgpException, messageMetadata, responseMessage);
      } else if (this.hasRegularResponseObject(responseMessage)) {
        LOGGER.info(
            "Calling application service function to handle response: {} with correlationUid: {}",
            messageMetadata.getMessageType(),
            messageMetadata.getCorrelationUid());

        this.handleMessage(messageMetadata, responseMessage, osgpException);
      } else {
        LOGGER.error(
            "No osgpException, yet dataObject ({}) is not of the regular type for handling response: {}",
            responseMessage.getDataObject() == null
                ? null
                : responseMessage.getDataObject().getClass().getName(),
            messageMetadata.getMessageType());

        this.handleError(
            new TechnicalException(
                ComponentType.DOMAIN_SMART_METERING,
                "Unexpected response data handling request.",
                null),
            messageMetadata);
      }

    } catch (final Exception e) {
      this.handleError(e, messageMetadata);
    }
  }

  /**
   * The {@code dataObject} in the {@code responseMessage} can either have a value that would
   * normally be returned as an answer, or it can contain an object that was used in the request
   * message (or other unexpected value).
   *
   * <p>The object from the request message is sometimes returned as object in the response message
   * to allow retries of requests without other knowledge of what was sent earlier.
   *
   * <p>To filter out these, or other unexpected situations that may occur in the future, each
   * message processor is supposed to check the response message for expected types of data objects.
   *
   * @param responseMessage the response message to be handled by this processor
   * @return {@code true} if {@code responseMessage} contains a {@code dataObject} that can be
   *     processed normally; {@code false} otherwise.
   */
  protected abstract boolean hasRegularResponseObject(final ResponseMessage responseMessage);

  protected abstract void handleMessage(
      MessageMetadata messageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException)
      throws FunctionalException;

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * <p>The response message is provided to allow manipulation of certain responses, for instance in
   * case the error has to be incorporated in the response instead of defining the response at its
   * own.
   *
   * @param e the exception.
   * @param messageMetadata the device message metadata.
   * @param responseMessage the response message.
   */
  /*
   * Suppressing S1130: Remove the declaration of thrown exception "FunctionalException"
   * FunctionalException is thrown from the implementation in subclass BundleResponseMessageProcessor.
   */
  @SuppressWarnings("squid:S1130")
  protected void handleError(
      final Exception e,
      final MessageMetadata messageMetadata,
      final ResponseMessage responseMessage)
      throws FunctionalException {
    if (responseMessage != null) {
      LOGGER.debug(
          "Handling error without using responseMessage for correlationUid: {}",
          responseMessage.getCorrelationUid());
    }
    this.handleError(e, messageMetadata);
  }

  /**
   * In case of an error, this function can be used to send a response containing the exception to
   * the web-service-adapter.
   *
   * @param e the exception.
   * @param messageMetadata the device message metadata.
   */
  protected void handleError(final Exception e, final MessageMetadata messageMetadata) {
    LOGGER.info(
        "handeling error: {} for message type: {}",
        e.getMessage(),
        messageMetadata.getMessageType(),
        e);
    final OsgpException osgpException = this.ensureOsgpException(e);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  protected OsgpException ensureOsgpException(final Exception e) {

    if (e instanceof OsgpException) {
      return (OsgpException) e;
    }

    return new TechnicalException(this.componentType, "An unknown error occurred", e);
  }
}
