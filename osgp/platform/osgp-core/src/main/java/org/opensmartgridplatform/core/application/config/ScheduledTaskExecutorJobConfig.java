// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.config;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.core.application.tasks.ScheduledTaskExecutorJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true)
public class ScheduledTaskExecutorJobConfig {

  @Value("${scheduling.scheduled.tasks.cron.expression}")
  private String cronExpressionScheduledTaskExecution;

  @Value("${scheduling.task.page.size}")
  private int scheduledTaskPageSize;

  @Value("${scheduling.task.pending.duration.max.seconds:3600}")
  private int scheduledTaskPendingDurationMaxSeconds;

  @Value("${scheduling.task.thread.pool.size:10}")
  private int scheduledTaskThreadPoolSize;

  @Autowired private OsgpScheduler osgpScheduler;

  @Bean
  public int scheduledTaskPageSize() {
    return this.scheduledTaskPageSize;
  }

  @Bean
  public long scheduledTaskPendingDurationMaxSeconds() {
    return this.scheduledTaskPendingDurationMaxSeconds;
  }

  @PostConstruct
  private void initializeScheduledJob() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(
        ScheduledTaskExecutorJob.class, this.cronExpressionScheduledTaskExecution);
  }

  public int getScheduledTaskThreadPoolSize() {
    return this.scheduledTaskThreadPoolSize;
  }
}
