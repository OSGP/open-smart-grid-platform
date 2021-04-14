/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DeviceRepository
    extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

  List<Device> findByIdIn(Collection<Long> ids);

  Device findByDeviceIdentification(String deviceIdentification);

  @Query(
      "SELECT d FROM Device d JOIN FETCH d.deviceFirmwareFiles dff JOIN FETCH dff.firmwareFile ff "
          + "JOIN FETCH ff.firmwareModules fffm JOIN FETCH fffm.firmwareModule fm "
          + "WHERE d.deviceIdentification = :deviceIdentification")
  Device findByDeviceIdentificationWithFirmware(
      @Param("deviceIdentification") String deviceIdentification);

  @Query(
      "SELECT d FROM Device d LEFT JOIN FETCH d.deviceFirmwareModules dfm LEFT JOIN FETCH dfm.firmwareModule fm "
          + "WHERE d.deviceIdentification = :deviceIdentification")
  Device findByDeviceIdentificationWithFirmwareModules(
      @Param("deviceIdentification") String deviceIdentification);

  List<Device> findByNetworkAddress(InetAddress address);

  // @formatter:off
  @Query(
      "SELECT d "
          + "FROM Device d "
          + "WHERE EXISTS "
          + "("
          + "    SELECT auth.id "
          + "    FROM d.authorizations auth "
          + "    WHERE auth.organisation = ?1"
          + ")")
  // @formatter:on
  Page<Device> findAllAuthorized(Organisation organisation, Pageable request);

  // @formatter:off
  @Query(
      "SELECT d "
          + "FROM Device d "
          + "WHERE NOT EXISTS "
          + "("
          + "    SELECT auth.id "
          + "    FROM d.authorizations auth "
          + "    WHERE auth.functionGroup = org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.OWNER"
          + ")")
  // @formatter:on
  List<Device> findDevicesWithNoOwner();

  // @formatter:off
  @Query(
      "SELECT d "
          + "FROM Device d "
          + "WHERE EXISTS "
          + "("
          + "    SELECT auth.id "
          + "    FROM d.authorizations auth "
          + "    WHERE auth.organisation = ?1 AND "
          + "        (auth.functionGroup = org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.OWNER OR "
          + "         auth.functionGroup = org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.INSTALLATION)"
          + ") AND "
          + "d.modificationTime >= ?2")
  // @formatter:on
  List<Device> findRecentDevices(Organisation organisation, Date fromDate);

  /*
   * We need these native queries below because these entities don't have an
   * Id.
   */
  @Modifying
  @Query(value = "delete from device_output_setting", nativeQuery = true)
  void deleteDeviceOutputSettings();

  List<Device> findByDeviceModelAndDeviceTypeAndInMaintenanceAndDeviceLifecycleStatus(
      DeviceModel deviceModel,
      String deviceType,
      boolean inMaintenance,
      DeviceLifecycleStatus deviceLifecycleStatus);
}
