//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
