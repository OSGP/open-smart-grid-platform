//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.repositories;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository
    extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

  List<Event> findByDeviceIdentification(String deviceIdentification);

  @Query(
      "SELECT d.id as device, MAX(e.dateTime) as dateTime "
          + "FROM Device d LEFT JOIN Event e ON d.deviceIdentification = e.deviceIdentification "
          + "WHERE d IN (?1) GROUP BY d.id")
  List<Object> findLatestEventForEveryDevice(Collection<Device> devices);

  List<Event> findTop2ByDeviceIdentificationOrderByDateTimeDesc(String deviceIdentification);

  List<Event> findByDateTimeBefore(Date date);

  Slice<Event> findByDateTimeBefore(Date date, Pageable pageable);

  @Modifying
  @Query("delete from Event e where e.id in :ids")
  void deleteBatchById(@Param("ids") List<Long> ids);
}
