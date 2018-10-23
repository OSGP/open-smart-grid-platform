/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
