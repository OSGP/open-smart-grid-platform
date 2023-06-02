//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.config.scheduling;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobDisabledConfig<J extends Job> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobDisabledConfig.class);

  private final Class<J> jobClass;
  private final OsgpScheduler osgpScheduler;

  public JobDisabledConfig(final Class<J> jobClass, final OsgpScheduler osgpScheduler) {
    this.jobClass = jobClass;
    this.osgpScheduler = osgpScheduler;
  }

  @PostConstruct
  protected void removeScheduledJob() throws SchedulerException {

    LOGGER.info("Job {} disabled, removing job.", this.jobClass);

    this.osgpScheduler.deleteScheduledJob(this.jobClass);
  }
}
