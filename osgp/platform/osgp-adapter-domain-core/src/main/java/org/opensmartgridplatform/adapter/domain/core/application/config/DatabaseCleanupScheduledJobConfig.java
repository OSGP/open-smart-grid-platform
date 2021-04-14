/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.config;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.domain.core.application.tasks.DeviceMessageCleanupJob;
import org.opensmartgridplatform.adapter.domain.core.application.tasks.EventCleanupJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-domain-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainCore/config}", ignoreResourceNotFound = true)
public class DatabaseCleanupScheduledJobConfig {

  @Value("${osgp.scheduling.job.database.cleanup.event.cron.expression}")
  private String cronExpressionDatabaseCleanupEvent;

  @Value("${osgp.scheduling.job.database.cleanup.device.message.cron.expression}")
  private String cronExpressionDatabaseCleanupDeviceMessage;

  @Autowired private OsgpScheduler osgpScheduler;

  @PostConstruct
  private void initializeScheduledJob() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(
        EventCleanupJob.class, this.cronExpressionDatabaseCleanupEvent);
    this.osgpScheduler.createAndScheduleJob(
        DeviceMessageCleanupJob.class, this.cronExpressionDatabaseCleanupDeviceMessage);
  }
}
