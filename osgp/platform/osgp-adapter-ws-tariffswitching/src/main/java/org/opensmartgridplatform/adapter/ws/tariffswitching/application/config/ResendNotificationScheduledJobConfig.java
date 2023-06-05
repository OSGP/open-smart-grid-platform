// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.tariffswitching.application.config;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.ws.shared.services.ResendNotificationJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-tariffswitching.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsTariffSwitching/config}",
    ignoreResourceNotFound = true)
public class ResendNotificationScheduledJobConfig {

  @Value("${tariffswitching.scheduling.job.resend.notification.cron.expression}")
  private String cronExpressionResendNotification;

  @Value("${tariffswitching.scheduling.job.resend.notification.maximum:2}")
  private short resendNotificationMaximum;

  @Value("${tariffswitching.scheduling.job.resend.notification.multiplier:2}")
  private int resendNotificationMultiplier;

  @Value("${tariffswitching.scheduling.job.resend.notification.resend.threshold.in.minutes:1}")
  private int resendThresholdInMinutes;

  @Value("${tariffswitching.scheduling.job.resend.notification.page.size:1}")
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
