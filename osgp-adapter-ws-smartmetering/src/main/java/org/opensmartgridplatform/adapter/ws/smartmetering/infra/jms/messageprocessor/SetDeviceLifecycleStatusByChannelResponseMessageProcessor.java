/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class SetDeviceLifecycleStatusByChannelResponseMessageProcessor extends DomainResponseMessageProcessor {

    protected SetDeviceLifecycleStatusByChannelResponseMessageProcessor() {
        super(MessageType.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL);
    }
}
