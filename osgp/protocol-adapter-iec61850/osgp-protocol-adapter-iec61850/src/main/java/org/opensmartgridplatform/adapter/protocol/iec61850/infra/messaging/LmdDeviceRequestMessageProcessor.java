// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.lmd.LmdDeviceService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class LmdDeviceRequestMessageProcessor extends BaseMessageProcessor {

  @Autowired protected LmdDeviceService deviceService;

  /**
   * Each MessageProcessor should register its MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected LmdDeviceRequestMessageProcessor(final MessageType messageType) {
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

  protected static MessageMetadata getMessageMetadata(
      final DeviceResponse deviceResponse, final String messageType) {
    return MessageMetadata.newBuilder()
        .withDeviceIdentification(deviceResponse.getDeviceIdentification())
        .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
        .withCorrelationUid(deviceResponse.getCorrelationUid())
        .withMessagePriority(deviceResponse.getMessagePriority())
        .withMessageType(messageType)
        .build();
  }
}
