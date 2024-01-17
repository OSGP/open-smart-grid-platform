// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.Type;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@Entity
@Table(name = "scheduled_task")
public class ScheduledTask extends AbstractScheduledTask {

  private static final long serialVersionUID = 1L;

  @Type(type = "java.io.Serializable")
  private Serializable messageData;

  public ScheduledTask() {
    // Default empty constructor for Hibernate.
  }

  public ScheduledTask(
      final MessageMetadata messageMetadata,
      final String domain,
      final String domainVersion,
      final Serializable messageData,
      final Timestamp scheduledTime) {
    this(
        messageMetadata,
        domain,
        domainVersion,
        messageData,
        scheduledTime,
        messageMetadata.getMaxScheduleTime() == null
            ? null
            : new Timestamp(messageMetadata.getMaxScheduleTime()));
  }

  public ScheduledTask(
      final MessageMetadata messageMetadata,
      final String domain,
      final String domainVersion,
      final Serializable messageData,
      final Timestamp scheduledTime,
      final Timestamp maxScheduleTime) {

    super(messageMetadata, domain, domainVersion, scheduledTime, maxScheduleTime);
    this.messageData = messageData;
  }

  public Serializable getMessageData() {
    return this.messageData;
  }
}
