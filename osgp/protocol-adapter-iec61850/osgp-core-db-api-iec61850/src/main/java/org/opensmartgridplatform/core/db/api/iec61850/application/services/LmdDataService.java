/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.iec61850.application.services;

import org.opensmartgridplatform.core.db.api.iec61850.entities.LightMeasurementDevice;
import org.opensmartgridplatform.core.db.api.iec61850.repositories.LmdDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "iec61850OsgpCoreDbApiTransactionManager", readOnly = true)
public class LmdDataService {

  @Autowired private LmdDataRepository lmdDataRepository;

  public LightMeasurementDevice findDevice(final String deviceIdentification) {
    return this.lmdDataRepository.findByDeviceIdentification(deviceIdentification);
  }
}
