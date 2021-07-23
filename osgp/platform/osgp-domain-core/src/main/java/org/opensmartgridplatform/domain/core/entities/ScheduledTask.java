/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Table;
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
    this(messageMetadata, domain, domainVersion, messageData, scheduledTime, null);
  }

  public ScheduledTask(
      final MessageMetadata messageMetadata,
      final String domain,
      final String domainVersion,
      final Serializable messageData,
      final Timestamp scheduledTime,
      final Timestamp maxScheduledTime) {

    super(messageMetadata, domain, domainVersion, scheduledTime, maxScheduledTime);
    this.messageData = messageData;
  }

  public Serializable getMessageData() {
    return this.messageData;
  }
}
