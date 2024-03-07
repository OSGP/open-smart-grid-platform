// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/** Scheduled task without the actual data */
@Entity
@Table(name = "scheduled_task")
public class ScheduledTaskWithoutData extends AbstractScheduledTask {

  private static final long serialVersionUID = 1L;

  ScheduledTaskWithoutData() {
    // Default empty constructor for Hibernate.
  }

  public ScheduledTaskWithoutData(
      final MessageMetadata messageMetadata,
      final String domain,
      final String domainVersion,
      final Timestamp scheduledTime,
      final Timestamp maxScheduleTime) {

    super(messageMetadata, domain, domainVersion, scheduledTime, maxScheduleTime);
  }
}
