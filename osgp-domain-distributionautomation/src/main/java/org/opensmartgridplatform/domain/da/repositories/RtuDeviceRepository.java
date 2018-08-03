/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.repositories;

import java.util.Date;
import java.util.List;

import org.opensmartgridplatform.domain.da.entities.RtuDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;

@Repository
public interface RtuDeviceRepository extends JpaRepository<RtuDevice, Long> {
    RtuDevice findById(long id);

    RtuDevice findByDeviceIdentification(String deviceIdentification);

    List<RtuDevice> findByDeviceLifecycleStatusAndLastCommunicationTimeBefore(
            DeviceLifecycleStatus deviceLifecycleStatus, Date lastCommunicationTime);
}
