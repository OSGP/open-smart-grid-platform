/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LightMeasurementDeviceRepository
    extends JpaRepository<LightMeasurementDevice, Long> {
  LightMeasurementDevice findByDeviceIdentification(String deviceIdentification);

  List<LightMeasurementDevice> findByProtocolInfoProtocolAndDeviceLifecycleStatus(
      String protocol, DeviceLifecycleStatus deviceLifecycleStatus);
}
