/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceMessageStatus;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;

@Service
public class DeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseService.class);

    public void handleDeviceMessageStatus(final DeviceMessageStatus status) throws TechnicalException {
        switch (status) {
        case FAILURE:
            LOGGER.info("Failure device message status received: {}", status);
            throw new TechnicalException(ComponentType.PROTOCOL_OSLP, "Device reports failure");
        case REJECTED:
            LOGGER.info("Rejected device message status received: {}", status);
            throw new TechnicalException(ComponentType.PROTOCOL_OSLP, "Device reports rejected");
        case OK:
            LOGGER.info("OK device message status received: {}", status);
            break;
        default:
            LOGGER.warn("Unknown device message status received: {}", status);
            break;
        }
    }
}
