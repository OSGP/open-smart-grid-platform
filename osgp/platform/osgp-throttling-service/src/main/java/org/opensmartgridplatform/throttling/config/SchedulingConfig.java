/*
 * Copyright 2021 Alliander N.V.
 */

package org.opensmartgridplatform.throttling.config;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.throttling.cleanup.ClientCleanUpJob;
import org.opensmartgridplatform.throttling.cleanup.PermitCleanUpJob;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulingConfig {
  @Value("${scheduling.task.cleanup.clients.cron.expression:0 0 3 * * ?}")
  private String clientsCronExpression;

  @Value("${scheduling.task.cleanup.permits.cron.expression:0 0 4 * * ?}")
  private String permitsCronExpression;

  private final OsgpScheduler osgpScheduler;

  public SchedulingConfig(final OsgpScheduler osgpScheduler) {
    this.osgpScheduler = osgpScheduler;
  }

  @PostConstruct
  public void initialize() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(ClientCleanUpJob.class, this.clientsCronExpression);
    this.osgpScheduler.createAndScheduleJob(PermitCleanUpJob.class, this.permitsCronExpression);
  }
}
