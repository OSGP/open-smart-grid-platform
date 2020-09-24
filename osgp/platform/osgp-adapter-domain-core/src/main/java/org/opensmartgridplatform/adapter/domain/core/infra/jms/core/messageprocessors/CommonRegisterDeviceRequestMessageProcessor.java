/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms.core.messageprocessors;

import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.core.infra.jms.DomainCoreDeviceRequestMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommonRegisterDeviceRequestMessageProcessor extends DomainCoreDeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRegisterDeviceRequestMessageProcessor.class);

    public CommonRegisterDeviceRequestMessageProcessor() {
        super(MessageType.REGISTER_DEVICE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.info("REGISTER_DEVICE message received. Ignore, since we act on CONFIRM_REGISTER_DEVICE");
    }

}
