/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.config;

import javax.annotation.PreDestroy;

import org.opensmartgridplatform.adapter.ws.shared.services.ResendNotificationJob;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataCleanupJob;
import org.opensmartgridplatform.shared.application.config.AbstractSchedulingConfig;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsDistributionAutomation/config}", ignoreResourceNotFound = true)
public class QuartzSchedulerConfig extends AbstractSchedulingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulerConfig.class);

    private static final String QUARTZ_SCHEDULER_NAME = "DistributionAutomationQuartzScheduler";

    private static final String KEY_CLEANUP_JOB_CRON_EXPRESSION = "distributionautomation.scheduling.job.cleanup.response.data.cron.expression";

    private static final String KEY_RESEND_NOTIFICATION_CRON_EXPRESSION = "distributionautomation.scheduling.job.resend.notification.cron.expression";

    @Value("${distributionautomation.scheduling.job.cleanup.response.data.retention.time.in.days}")
    private int cleanupJobRetentionTimeInDays;

    @Value("${distributionautomation.scheduling.job.resend.notification.maximum}")
    private short resendNotificationMaximum;

    @Value("${distributionautomation.scheduling.job.resend.notification.multiplier}")
    private int resendNotificationMultiplier;

    @Value("${distributionautomation.scheduling.job.resend.notification.resend.threshold.in.minutes}")
    private int resendThresholdInMinutes;

    @Value("${distributionautomation.scheduling.job.resend.notification.page.size}")
    private int resendPageSize;

    @Autowired
    private Scheduler quartzScheduler;

    @Bean
    public int cleanupJobRetentionTimeInDays() {
        return this.cleanupJobRetentionTimeInDays;
    }

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

    @Bean
    public Scheduler quartzScheduler() throws SchedulerException {
        final SchedulingConfigProperties schedulingConfigProperties = SchedulingConfigProperties.newBuilder()
                .withThreadCountKey(KEY_QUARTZ_SCHEDULER_THREAD_COUNT).withJobStoreDbUrl(this.getDatabaseUrl())
                .withJobName(QUARTZ_SCHEDULER_NAME).withJobStoreDbUsername(this.databaseUsername)
                .withJobStoreDbPassword(this.databasePassword).withJobStoreDbDriver(this.databaseDriver).build();

        this.quartzScheduler = this.constructBareScheduler(schedulingConfigProperties);

        final String cronExpressionResponseCleanup = this.environment
                .getRequiredProperty(KEY_CLEANUP_JOB_CRON_EXPRESSION);
        this.createAndScheduleJob(this.quartzScheduler, ResponseDataCleanupJob.class, cronExpressionResponseCleanup);

        final String cronExpressionResendNotification = this.environment
                .getRequiredProperty(KEY_RESEND_NOTIFICATION_CRON_EXPRESSION);
        this.createAndScheduleJob(this.quartzScheduler, ResendNotificationJob.class, cronExpressionResendNotification);

        LOGGER.info("Starting {}.", QUARTZ_SCHEDULER_NAME);
        this.quartzScheduler.start();

        return this.quartzScheduler;
    }

    @PreDestroy
    public void stopQuartzScheduler() throws SchedulerException {
        LOGGER.info("Stopping {}.", QUARTZ_SCHEDULER_NAME);
        this.quartzScheduler.shutdown(true);
        this.quartzScheduler.clear();
    }

}
