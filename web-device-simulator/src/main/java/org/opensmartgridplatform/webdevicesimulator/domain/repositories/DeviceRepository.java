/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;

/**
 * Repository for device entities
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT d FROM Device d ORDER BY d.id ASC")
    List<Device> findAllOrderById();

    Device findByDeviceUid(String deviceUid);

    List<Device> findByHasEveningMorningBurner(Boolean hasEveningMorningBurner);
}
