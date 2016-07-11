/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.logging.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.logging.domain.entities.DeviceLogItem;

@Repository
public interface DeviceLogItemRepository extends JpaRepository<DeviceLogItem, Long> {
    Page<DeviceLogItem> findByDeviceIdentificationInOrderByCreationTimeDesc(List<String> deviceIdentification,
            Pageable pagable);

    Page<DeviceLogItem> findByDeviceIdentification(String deviceIdentification, Pageable pagable);
}
