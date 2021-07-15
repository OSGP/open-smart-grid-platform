/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledTaskService {

  @Autowired private ScheduledTaskRepository scheduledTaskRepository;

  public ScheduledTask findByCorrelationUid(final String correlationUid) {

    return this.scheduledTaskRepository.findByCorrelationUid(correlationUid);
  }

  @Transactional
  public ScheduledTask saveScheduledTask(final ScheduledTask scheduledTask) {

    return this.scheduledTaskRepository.save(scheduledTask);
  }

  @Transactional
  public void deleteScheduledTask(final ScheduledTask scheduledTask) {

    this.scheduledTaskRepository.delete(scheduledTask);
  }
}
