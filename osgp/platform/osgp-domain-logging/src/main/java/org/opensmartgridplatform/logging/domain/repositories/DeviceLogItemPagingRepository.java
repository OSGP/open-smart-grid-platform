//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.logging.domain.repositories;

import java.util.Date;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceLogItemPagingRepository extends JpaRepository<DeviceLogItem, Long> {
  Page<DeviceLogItem> findByDeviceIdentification(String deviceIdentification, Pageable pagable);

  // Added only for testing
  @Modifying
  @Query(value = "UPDATE DeviceLogItem SET modificationTime = :modificationTime WHERE id = :id")
  int setModificationTime(@Param("id") long id, @Param("modificationTime") Date modificationTime);
}
