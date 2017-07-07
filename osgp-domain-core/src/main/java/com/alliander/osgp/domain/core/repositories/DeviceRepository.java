/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.repositories;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

    Device findByDeviceIdentification(String deviceIdentification);

    List<Device> findByNetworkAddress(InetAddress address);

    @Query("SELECT d " + "FROM Device d " + "WHERE EXISTS " + "(" + "	SELECT auth.id " + "	FROM d.authorizations auth "
            + "	WHERE auth.organisation = ?1" + ")")
    Page<Device> findAllAuthorized(Organisation organisation, Pageable request);

    @Query("SELECT d " + "FROM Device d " + "WHERE NOT EXISTS " + "(" + "	SELECT auth.id "
            + "	FROM d.authorizations auth "
            + "	WHERE auth.functionGroup = com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.OWNER" + ")")
    List<Device> findDevicesWithNoOwner();

    @Query("SELECT d " + "FROM Device d " + "WHERE EXISTS " + "(" + "	SELECT auth.id " + "	FROM d.authorizations auth "
            + "	WHERE auth.organisation = ?1 AND "
            + "		(auth.functionGroup = com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.OWNER OR "
            + "		 auth.functionGroup = com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.INSTALLATION)"
            + ") AND " + "d.modificationTime >= ?2")
    List<Device> findRecentDevices(Organisation organisation, Date fromDate);

    /*
     * We need these native queries below because these entities dont have an Id
     */
    @Modifying
    @Query(value = "delete from device_output_setting", nativeQuery = true)
    void deleteDeviceOutputSettings();

    List<Device> findByDeviceModelAndDeviceTypeAndInMaintenance(DeviceModel deviceModel, String deviceType,
            boolean inMantenance);
}
