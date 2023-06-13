// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.repositories;

import java.sql.Timestamp;
import java.util.List;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
  List<ScheduledTask> findByStatusAndScheduledTimeLessThan(
      ScheduledTaskStatusType status, Timestamp currentTimestamp, Pageable pageable);

  ScheduledTask findByCorrelationUid(String correlationUid);
}
