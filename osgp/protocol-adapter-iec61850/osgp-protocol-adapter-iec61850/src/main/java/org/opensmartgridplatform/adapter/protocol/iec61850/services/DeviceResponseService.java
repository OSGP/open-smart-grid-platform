/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.services;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeviceResponseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseService.class);

  /**
   * Processes the given {@link DeviceMessageStatus} and throws appropriate exceptions if the status
   * is not OK.
   *
   * @param status The {@link DeviceMessageStatus} to check.
   * @throws TechnicalException In case the status equals FAILURE or REJECTED.
   */
  public void handleDeviceMessageStatus(final DeviceMessageStatus status)
      throws TechnicalException {
    switch (status) {
      case FAILURE:
        LOGGER.info("Failure device message status received: {}", status);
        throw new TechnicalException(ComponentType.PROTOCOL_IEC61850, "Device reports failure.");
      case REJECTED:
        LOGGER.info("Rejected device message status received: {}", status);
        throw new TechnicalException(ComponentType.PROTOCOL_IEC61850, "Device reports rejected.");
      case OK:
        LOGGER.info("OK device message status received: {}", status);
        break;
      default:
        LOGGER.warn("Unknown device message status received: {}", status);
        break;
    }
  }
}
