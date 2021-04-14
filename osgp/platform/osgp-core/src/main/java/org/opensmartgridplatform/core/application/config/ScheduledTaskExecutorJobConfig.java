/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import javax.annotation.PostConstruct;
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

  @Autowired private OsgpScheduler osgpScheduler;

  @Bean
  public int scheduledTaskPageSize() {
    return this.scheduledTaskPageSize;
  }

  @PostConstruct
  private void initializeScheduledJob() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(
        ScheduledTaskExecutorJob.class, this.cronExpressionScheduledTaskExecution);
  }
}
