/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.domain.repositories;

import java.util.Date;

import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceLogItemSlicingRepository extends JpaRepository<DeviceLogItem, Long> {
    Slice<DeviceLogItem> findByDeviceIdentification(String deviceIdentification, Pageable pagable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndOrganisationIdentification(String deviceIdentification, String organisationIdentification, Pageable pagable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndCreationTimeBetween(String deviceIdentification, Date startDate, Date endDate, Pageable pagable);
    
    Slice<DeviceLogItem> findByOrganisationIdentification(String organisationIdentification, Pageable pagable);
    
    Slice<DeviceLogItem> findByOrganisationIdentificationAndCreationTimeBetween(String organisationIdentification, Date startDate, Date endDate, Pageable pagable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndOrganisationIdentificationAndCreationTimeBetween(String deviceIdentification, String organisationIdentification, Date startDate, Date endDate, Pageable pagable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndOrganisationIdentificationAndCreationTimeAfter(String deviceIdentification, String organisationIdentification, Date startDate, Pageable pageable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndOrganisationIdentificationAndCreationTimeBefore(String deviceIdentification, String organisationIdentification, Date endDate, Pageable pageable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndCreationTimeAfter(String deviceIdentification, Date startDate, Pageable pagable);
    
    Slice<DeviceLogItem> findByDeviceIdentificationAndCreationTimeBefore(String deviceIdentification, Date endDate, Pageable pagable);
    
    Slice<DeviceLogItem> findByOrganisationIdentificationAndCreationTimeAfter(String organisationIdentification, Date startDate, Pageable pagable);
    
    Slice<DeviceLogItem> findByOrganisationIdentificationAndCreationTimeBefore(String organisationIdentification, Date endDate, Pageable pagable);

    Slice<DeviceLogItem> findByCreationTimeAfter(Date startDate, Pageable pageable);
    
    Slice<DeviceLogItem> findByCreationTimeBefore(Date endDate, Pageable pageable);
    
    Slice<DeviceLogItem> findByCreationTimeBetween(Date startDate, Date endDate, Pageable pageable);
    
    Slice<DeviceLogItem> findAllBy(Pageable pageable);
}
