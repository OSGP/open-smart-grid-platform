// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
