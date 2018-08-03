/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;

@Repository
public interface SsldRepository extends JpaRepository<Ssld, Long> {
    Ssld findByDeviceIdentification(String deviceIdentification);

    List<Ssld> findByLightMeasurementDeviceAndIsActivatedTrueAndInMaintenanceFalseAndProtocolInfoNotNullAndNetworkAddressNotNullAndTechnicalInstallationDateNotNullAndDeviceLifecycleStatus(
            LightMeasurementDevice lightMeasurementDevice, DeviceLifecycleStatus deviceLifecycleStatus);
}
