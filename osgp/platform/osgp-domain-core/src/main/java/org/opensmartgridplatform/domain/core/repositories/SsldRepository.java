// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SsldRepository extends JpaRepository<Ssld, Long> {
  Ssld findByDeviceIdentification(String deviceIdentification);

  @Query(
      "SELECT new org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice(ssld.deviceIdentification, "
          + "ssld.networkAddress, ssld.cdmaSettings.mastSegment, ssld.cdmaSettings.batchNumber) "
          + "FROM Ssld ssld WHERE lightMeasurementDevice = :lightMeasurementDevice "
          + "AND isActivated=true AND inMaintenance=false AND protocolInfo IS NOT NULL "
          + "AND networkAddress IS NOT NULL AND technicalInstallationDate IS NOT NULL "
          + "AND deviceLifecycleStatus = :deviceLifecycleStatus ")
  List<CdmaDevice> findCdmaBatchDevicesInUseForLmd(
      @Param("lightMeasurementDevice") LightMeasurementDevice lightMeasurementDevice,
      @Param("deviceLifecycleStatus") DeviceLifecycleStatus deviceLifecycleStatus);
}
