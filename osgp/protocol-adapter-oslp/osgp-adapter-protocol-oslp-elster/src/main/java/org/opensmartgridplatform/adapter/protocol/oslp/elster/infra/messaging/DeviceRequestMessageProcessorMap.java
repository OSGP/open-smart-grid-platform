//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component("protocolOslpDeviceRequestMessageProcessorMap")
public class DeviceRequestMessageProcessorMap extends BaseMessageProcessorMap {

  public DeviceRequestMessageProcessorMap() {
    super("DeviceRequestMessageProcessorMap");
  }

  public OslpEnvelopeProcessor getOslpEnvelopeProcessor(final MessageType messageType) {
    return (OslpEnvelopeProcessor) this.messageProcessors.get(messageType);
  }
}
