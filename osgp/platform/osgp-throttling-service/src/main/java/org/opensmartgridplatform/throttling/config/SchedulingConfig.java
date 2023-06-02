//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.config;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.throttling.cleanup.PermitCleanUpJob;
import org.opensmartgridplatform.throttling.cleanup.ReinitializeStateJob;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulingConfig {
  @Value("${scheduling.task.cleanup.permits.cron.expression:0 0/30 * * * ?}")
  private String permitsCronExpression;

  @Value("${scheduling.task.reinitialize.state.cron.expression:30 0/30 * * * ?}")
  private String resetDbStateCronExpression;

  private final OsgpScheduler osgpScheduler;

  public SchedulingConfig(final OsgpScheduler osgpScheduler) {
    this.osgpScheduler = osgpScheduler;
  }

  @PostConstruct
  public void initialize() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(PermitCleanUpJob.class, this.permitsCronExpression);
    this.osgpScheduler.createAndScheduleJob(
        ReinitializeStateJob.class, this.resetDbStateCronExpression);
  }
}
