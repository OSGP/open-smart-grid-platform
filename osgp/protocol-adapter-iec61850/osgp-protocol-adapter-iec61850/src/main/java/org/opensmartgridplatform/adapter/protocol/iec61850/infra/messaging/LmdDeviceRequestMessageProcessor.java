/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.annotation.PostConstruct;
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
    return new MessageMetadata.Builder()
        .withDeviceIdentification(deviceResponse.getDeviceIdentification())
        .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
        .withCorrelationUid(deviceResponse.getCorrelationUid())
        .withMessagePriority(deviceResponse.getMessagePriority())
        .withMessageType(messageType)
        .build();
  }
}
