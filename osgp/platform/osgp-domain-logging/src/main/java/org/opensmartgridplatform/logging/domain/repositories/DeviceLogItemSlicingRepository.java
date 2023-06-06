// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.domain.repositories;

import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceLogItemSlicingRepository
    extends JpaRepository<DeviceLogItem, Long>, JpaSpecificationExecutor<DeviceLogItem> {
  Slice<DeviceLogItem> findByModificationTimeBefore(Date endDate, Pageable pageable);

  @Modifying
  @Query("delete from DeviceLogItem d where d.id in :ids")
  void deleteBatchById(@Param("ids") List<Long> ids);
}
