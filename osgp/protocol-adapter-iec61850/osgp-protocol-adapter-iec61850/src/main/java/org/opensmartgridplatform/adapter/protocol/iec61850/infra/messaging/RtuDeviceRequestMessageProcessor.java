//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuDeviceService;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class RtuDeviceRequestMessageProcessor extends BaseMessageProcessor {

  protected static final String UNEXPECTED_EXCEPTION =
      "Unexpected exception while retrieving response message";

  @Autowired protected RtuDeviceService deviceService;

  /**
   * Each MessageProcessor should register it's MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected RtuDeviceRequestMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.iec61850RequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
  }
}
