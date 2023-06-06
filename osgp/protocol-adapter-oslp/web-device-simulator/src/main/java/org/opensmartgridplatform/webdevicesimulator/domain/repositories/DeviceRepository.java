// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.domain.repositories;

import java.util.List;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Repository for device entities */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

  @Query("SELECT d FROM Device d ORDER BY d.id ASC")
  List<Device> findAllOrderById();

  Device findByDeviceUid(String deviceUid);

  Page<Device> findByDeviceIdentification(String deviceIdentification, Pageable pageable);

  List<Device> findByHasEveningMorningBurner(Boolean hasEveningMorningBurner);
}
