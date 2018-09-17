/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core;

import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.springframework.stereotype.Component;

@Component("protocolDlmsOsgpResponseMessageProcessorMap")
public class OsgpResponseMessageProcessorMap extends BaseMessageProcessorMap {

    public OsgpResponseMessageProcessorMap() {
        super("OsgpResponseMessageProcessorMap");
    }

}
