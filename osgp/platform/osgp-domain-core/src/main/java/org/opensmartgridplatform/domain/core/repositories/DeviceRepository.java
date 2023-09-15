// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Collection;
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

  @Query(
      "SELECT d "
          + "FROM Device d "
          + "WHERE EXISTS "
          + "("
          + "    SELECT auth.id "
          + "    FROM d.authorizations auth "
          + "    WHERE auth.organisation = ?1"
          + ")")
  Page<Device> findAllAuthorized(Organisation organisation, Pageable request);

  @Query(
      "SELECT d "
          + "FROM Device d "
          + "WHERE NOT EXISTS "
          + "("
          + "    SELECT auth.id "
          + "    FROM d.authorizations auth "
          + "    WHERE auth.functionGroup = org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.OWNER"
          + ")")
  List<Device> findDevicesWithNoOwner();

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
  List<Device> findRecentDevices(Organisation organisation, Instant fromDate);

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

  @Transactional
  @Modifying
  @Query(
      value =
          "UPDATE Device d SET d.lastSuccessfulConnectionTimestamp = now(), d.failedConnectionCount = 0"
              + " WHERE d.deviceIdentification = ?1")
  int updateConnectionDetailsToSuccess(String deviceIdentification);

  @Transactional
  @Modifying
  @Query(
      value =
          "UPDATE Device d SET d.lastFailedConnectionTimestamp = now(), d.failedConnectionCount = d.failedConnectionCount+1"
              + " WHERE d.deviceIdentification = ?1")
  int updateConnectionDetailsToFailure(String deviceIdentification);
}
