/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.config;

import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResponseDataCleanupSchedulingConfig;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataCleanupJob;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties.Builder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsPublicLighting/config}", ignoreResourceNotFound = true)
public class PublicLightingReponseDataCleanUpSchedulingConfig extends AbstractResponseDataCleanupSchedulingConfig {

    private static final String KEY_CLEANUP_JOB_CRON_EXPRESSION = "publiclighting.scheduling.job.cleanup.response.data.cron.expression";
    private static final String KEY_CLEANUP_JOB_THREAD_COUNT = "publiclighting.scheduling.job.cleanup.response.data.thread.count";

    @Value("${publiclighting.scheduling.job.cleanup.response.data.retention.time.in.days}")
    private int cleanupJobRetentionTimeInDays;

    @Override
    @Bean
    public int cleanupJobRetentionTimeInDays() {
        return this.cleanupJobRetentionTimeInDays;
    }

    @Override
    @Bean(destroyMethod = "shutdown")
    public Scheduler cleanupResponseDataScheduler() throws SchedulerException {
        return this.constructScheduler(
                this.abstractSchedulingConfigBuilder().withJobClass(ResponseDataCleanupJob.class).build());
    }

    @Override
    protected Builder abstractSchedulingConfigBuilder() {
        return SchedulingConfigProperties.newBuilder().withThreadCountKey(KEY_CLEANUP_JOB_THREAD_COUNT)
                .withCronExpressionKey(KEY_CLEANUP_JOB_CRON_EXPRESSION).withJobStoreDbUrl(this.getDatabaseUrl())
                .withJobStoreDbUsername(this.databaseUsername).withJobStoreDbPassword(this.databasePassword)
                .withJobStoreDbDriver(this.databaseDriver);
    }
}
