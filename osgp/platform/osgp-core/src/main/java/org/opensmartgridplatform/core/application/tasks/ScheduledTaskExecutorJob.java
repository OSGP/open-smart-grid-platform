// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.tasks;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class ScheduledTaskExecutorJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskExecutorJob.class);

  @Autowired private ScheduledTaskExecutorService scheduledTaskExecutorService;

  @Override
  public void execute(final JobExecutionContext context) throws JobExecutionException {
    LOGGER.info("Quartz triggered processing of scheduled task execution.");
    this.scheduledTaskExecutorService.processScheduledTasks();
  }
}
