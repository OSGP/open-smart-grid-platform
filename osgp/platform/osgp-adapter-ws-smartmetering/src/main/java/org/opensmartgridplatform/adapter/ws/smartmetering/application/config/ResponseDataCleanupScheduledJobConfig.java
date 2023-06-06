// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataCleanupJob;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ResponseUrlDataCleanupJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class ResponseDataCleanupScheduledJobConfig {

  @Value("${smartmetering.scheduling.job.cleanup.response.data.cron.expression}")
  private String cronExpressionResponseCleanup;

  @Value("${smartmetering.scheduling.job.cleanup.response.data.retention.time.in.days}")
  private int cleanupJobRetentionTimeInDays;

  @Autowired private OsgpScheduler osgpScheduler;

  @Bean
  public int cleanupJobRetentionTimeInDays() {
    return this.cleanupJobRetentionTimeInDays;
  }

  @PostConstruct
  private void initializeScheduledJob() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(
        ResponseDataCleanupJob.class, this.cronExpressionResponseCleanup);

    this.osgpScheduler.createAndScheduleJob(
        ResponseUrlDataCleanupJob.class, this.cronExpressionResponseCleanup);
  }
}
