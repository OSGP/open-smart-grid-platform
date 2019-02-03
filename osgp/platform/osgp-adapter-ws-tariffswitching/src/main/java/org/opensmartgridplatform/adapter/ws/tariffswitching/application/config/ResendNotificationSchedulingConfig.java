/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.config;

import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResendNotificationSchedulingConfig;
import org.opensmartgridplatform.adapter.ws.shared.services.ResendNotificationJob;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@PropertySource("classpath:osgp-adapter-ws-tariffswitching.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsTariffSwitching/config}", ignoreResourceNotFound = true)
public class ResendNotificationSchedulingConfig extends AbstractResendNotificationSchedulingConfig {

    private static final String KEY_RESEND_NOTIFICATION_CRON_EXPRESSION = "tariffswitching.scheduling.job.resend.notification.cron.expression";
    private static final String KEY_RESEND_NOTIFICATION_THREAD_COUNT = "tariffswitching.scheduling.job.resend.notification.thread.count";

    @Value("${tariffswitching.scheduling.job.resend.notification.maximum:2}")
    private short resendNotificationMaximum;

    @Value("${tariffswitching.scheduling.job.resend.notification.multiplier:2}")
    private int resendNotificationMultiplier;

    @Value("${tariffswitching.scheduling.job.resend.notification.resend.threshold.in.minutes:1}")
    private int resendThresholdInMinutes;

    @Value("${tariffswitching.scheduling.job.resend.notification.page.size:1}")
    private int resendPageSize;

    @Override
    @Bean
    public short resendNotificationMaximum() {
        return this.resendNotificationMaximum;
    }

    @Override
    @Bean
    public int resendNotificationMultiplier() {
        return this.resendNotificationMultiplier;
    }

    @Override
    @Bean
    public int resendThresholdInMinutes() {
        return this.resendThresholdInMinutes;
    }

    @Override
    @Bean
    public int resendPageSize() {
        return this.resendPageSize;
    }

    @Override
    @Bean(destroyMethod = "shutdown")
    public Scheduler resendNotificationScheduler() throws SchedulerException {

        final SchedulingConfigProperties schedulingConfigProperties = SchedulingConfigProperties.newBuilder()
                .withJobClass(ResendNotificationJob.class).withThreadCountKey(KEY_RESEND_NOTIFICATION_THREAD_COUNT)
                .withCronExpressionKey(KEY_RESEND_NOTIFICATION_CRON_EXPRESSION).withJobStoreDbUrl(this.getDatabaseUrl())
                .withJobStoreDbUsername(this.databaseUsername).withJobStoreDbPassword(this.databasePassword)
                .withJobStoreDbDriver(this.databaseDriver).build();

        return this.constructScheduler(schedulingConfigProperties);
    }
}