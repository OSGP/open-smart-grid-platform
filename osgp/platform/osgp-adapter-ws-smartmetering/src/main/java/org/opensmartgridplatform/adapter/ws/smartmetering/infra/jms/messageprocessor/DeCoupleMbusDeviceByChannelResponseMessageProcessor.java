/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class DeCoupleMbusDeviceByChannelResponseMessageProcessor extends DomainResponseMessageProcessor {

    protected DeCoupleMbusDeviceByChannelResponseMessageProcessor() {
        super(MessageType.DE_COUPLE_MBUS_DEVICE_BY_CHANNEL);
    }
}
