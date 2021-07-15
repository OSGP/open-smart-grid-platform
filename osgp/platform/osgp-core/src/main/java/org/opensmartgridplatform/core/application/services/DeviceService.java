/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

  @Autowired private DeviceRepository deviceRepository;

  public Device findByDeviceIdentification(final String deviceIdentification) {

    return this.deviceRepository.findByDeviceIdentification(deviceIdentification);
  }
}
