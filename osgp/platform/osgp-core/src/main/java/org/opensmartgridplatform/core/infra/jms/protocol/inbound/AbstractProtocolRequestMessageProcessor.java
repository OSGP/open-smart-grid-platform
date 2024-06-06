// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.inbound;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class AbstractProtocolRequestMessageProcessor implements MessageProcessor {

  /** The map of message processor instances. */
  @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap")
  @Autowired
  protected MessageProcessorMap protocolRequestMessageProcessorMap;

  /** The message type that a message processor implementation can handle. */
  protected MessageType messageType;

  /**
   * Construct a message processor instance by passing in the message type.
   *
   * @param messageType The message type a message processor can handle.
   */
  protected AbstractProtocolRequestMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.protocolRequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
  }
}
