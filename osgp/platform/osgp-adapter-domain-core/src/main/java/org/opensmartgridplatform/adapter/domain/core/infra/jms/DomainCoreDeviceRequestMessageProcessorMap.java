/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms;

import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class DomainCoreDeviceRequestMessageProcessorMap extends BaseMessageProcessorMap {

  public DomainCoreDeviceRequestMessageProcessorMap() {
    super("DomainCoreDeviceRequestMessageProcessorMap");
  }

  public MessageProcessor getMessageProcessor(final MessageType messageType) {
    return this.messageProcessors.get(messageType);
  }
}
