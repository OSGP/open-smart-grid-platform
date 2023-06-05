// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class BaseRequestMessageProcessor extends AbstractRequestMessageProcessor
    implements MessageProcessor {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseRequestMessageProcessor.class);

  /** The map of message processor instances. */
  protected MessageProcessorMap messageProcessorMap;

  /** The message type that a message processor implementation can handle. */
  private final MessageType messageType;

  /**
   * Construct a message processor instance by passing in the message type.
   *
   * @param messageProcessorMap
   * @param messageType The message type a message processor can handle.
   */
  protected BaseRequestMessageProcessor(
      final MessageProcessorMap messageProcessorMap, final MessageType messageType) {
    this.messageProcessorMap = messageProcessorMap;
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors. The key for the HashMap is the integer
   * value of the enumeration member.
   */
  @PostConstruct
  public void init() {
    this.messageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  /**
   * Indicates if the message processor contains a dataobject that should be handled. Normally
   * requests do contains some data, so the default is TRUE.
   *
   * @return Does the message contain a dataobject to be processed.
   */
  public boolean messageContainsDataObject() {
    return true;
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    Object dataObject = null;

    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);

    try {
      dataObject = message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      LOGGER.debug("message metadata: {}", messageMetadata);
      return;
    }

    try {
      LOGGER.info("Calling application service function: {}", messageMetadata.getMessageType());
      if (this.messageContainsDataObject()) {
        this.handleMessage(messageMetadata, dataObject);
      } else {
        this.handleMessage(messageMetadata);
      }

    } catch (final Exception e) {
      this.handleError(e, messageMetadata);
    }
  }
}
