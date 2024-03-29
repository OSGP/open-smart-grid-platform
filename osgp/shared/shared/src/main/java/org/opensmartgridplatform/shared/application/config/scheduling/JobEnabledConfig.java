// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.scheduling;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobEnabledConfig<J extends Job> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobEnabledConfig.class);

  private final Class<J> jobClass;
  private final OsgpScheduler osgpScheduler;
  private final String jobCronExpression;

  public JobEnabledConfig(
      final Class<J> jobClass, final OsgpScheduler osgpScheduler, final String jobCronExpression) {
    this.jobClass = jobClass;
    this.osgpScheduler = osgpScheduler;
    this.jobCronExpression = jobCronExpression;
  }

  @PostConstruct
  private void initializeScheduledJob() throws SchedulerException {

    LOGGER.info("Job {} enabled, scheduling job.", this.jobClass);

    this.osgpScheduler.createAndScheduleJob(this.jobClass, this.jobCronExpression);
  }
}
