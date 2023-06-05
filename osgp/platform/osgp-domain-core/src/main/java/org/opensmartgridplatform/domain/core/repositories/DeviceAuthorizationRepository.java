// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceAuthorizationRepository extends JpaRepository<DeviceAuthorization, Long> {
  List<DeviceAuthorization> findByDevice(Device device);

  List<DeviceAuthorization> findByDeviceAndFunctionGroup(
      Device device, DeviceFunctionGroup functionGroup);

  List<DeviceAuthorization> findByOrganisation(Organisation organisation);

  List<DeviceAuthorization> findByOrganisationAndDevice(Organisation organisation, Device device);

  @Query(
      "select da "
          + "from DeviceAuthorization da "
          + "where da.device = ?1 "
          + "and (?2 in "
          + "        (select da.organisation "
          + "         from DeviceAuthorization da "
          + "         where da.device = ?1 and da.functionGroup = org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.OWNER) "
          + "     or da.organisation = ?2)")
  List<DeviceAuthorization> findByDeviceForOrganisation(Device device, Organisation organisation);

  void deleteByDeviceAndFunctionGroupAndOrganisation(
      Device device, DeviceFunctionGroup functionGroup, Organisation organisation);
}
