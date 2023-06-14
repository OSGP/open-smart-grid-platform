// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
