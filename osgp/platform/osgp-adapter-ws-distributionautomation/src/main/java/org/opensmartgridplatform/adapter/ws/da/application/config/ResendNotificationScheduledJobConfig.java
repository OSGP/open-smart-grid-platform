// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.config;

import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.shared.services.ResendNotificationJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsDistributionAutomation/config}",
    ignoreResourceNotFound = true)
public class ResendNotificationScheduledJobConfig {

  @Value("${distributionautomation.scheduling.job.resend.notification.cron.expression}")
  private String cronExpressionResendNotification;

  @Value("${distributionautomation.scheduling.job.resend.notification.maximum}")
  private short resendNotificationMaximum;

  @Value("${distributionautomation.scheduling.job.resend.notification.multiplier}")
  private int resendNotificationMultiplier;

  @Value("${distributionautomation.scheduling.job.resend.notification.resend.threshold.in.minutes}")
  private int resendThresholdInMinutes;

  @Value("${distributionautomation.scheduling.job.resend.notification.page.size}")
  private int resendPageSize;

  @Autowired private OsgpScheduler osgpScheduler;

  @Bean
  public short resendNotificationMaximum() {
    return this.resendNotificationMaximum;
  }

  @Bean
  public int resendNotificationMultiplier() {
    return this.resendNotificationMultiplier;
  }

  @Bean
  public int resendThresholdInMinutes() {
    return this.resendThresholdInMinutes;
  }

  @Bean
  public int resendPageSize() {
    return this.resendPageSize;
  }

  @PostConstruct
  private void initializeScheduledJob() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(
        ResendNotificationJob.class, this.cronExpressionResendNotification);
  }
}
