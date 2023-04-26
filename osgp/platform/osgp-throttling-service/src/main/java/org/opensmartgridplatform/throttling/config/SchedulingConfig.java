/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.throttling.config;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.throttling.cleanup.PermitCleanUpJob;
import org.opensmartgridplatform.throttling.cleanup.ResetDbStateJob;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulingConfig {
  @Value("${scheduling.task.cleanup.permits.cron.expression:0 0/30 * * * ?}")
  private String permitsCronExpression;

  @Value("${scheduling.task.reset.db.state.cron.expression:0 0 * * * ?}")
  private String resetDbStateCronExpression;

  private final OsgpScheduler osgpScheduler;

  public SchedulingConfig(final OsgpScheduler osgpScheduler) {
    this.osgpScheduler = osgpScheduler;
  }

  @PostConstruct
  public void initialize() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(PermitCleanUpJob.class, this.permitsCronExpression);
    this.osgpScheduler.createAndScheduleJob(ResetDbStateJob.class, this.resetDbStateCronExpression);
  }
}
