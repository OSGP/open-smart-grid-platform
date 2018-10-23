/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataCleanupJob;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ResponseUrlDataCleanupJob;
import org.opensmartgridplatform.shared.application.config.AbstractSchedulingConfig;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties.Builder;

@EnableScheduling
@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class SchedulingConfig extends AbstractSchedulingConfig {

    private static final String KEY_CLEANUP_JOB_CRON_EXPRESSION = "smartmetering.scheduling.job.cleanup.response.data.cron.expression";
    private static final String KEY_CLEANUP_JOB_THREAD_COUNT = "smartmetering.scheduling.job.cleanup.response.data.thread.count";

    @Value("${db.driver}")
    private String databaseDriver;

    @Value("${db.password}")
    private String databasePassword;

    @Value("${db.protocol}")
    private String databaseProtocol;

    @Value("${db.host}")
    private String databaseHost;

    @Value("${db.port}")
    private String databasePort;

    @Value("${db.name}")
    private String databaseName;

    @Value("${db.username}")
    private String databaseUsername;

    @Value("${smartmetering.scheduling.job.cleanup.response.data.retention.time.in.days}")
    private int cleanupJobRetentionTimeInDays;

    @Bean
    public int cleanupJobRetentionTimeInDays() {
        return this.cleanupJobRetentionTimeInDays;
    }

    @Bean(destroyMethod = "shutdown")
    public Scheduler cleanupResponseDataScheduler() throws SchedulerException {
        return this.constructScheduler(
                this.abstractSchedulingConfigBuilder().withJobClass(ResponseDataCleanupJob.class).build());
    }

    @Bean(destroyMethod = "shutdown")
    public Scheduler cleanupResponseUrlDataScheduler() throws SchedulerException {
        return this.constructScheduler(
                this.abstractSchedulingConfigBuilder().withJobClass(ResponseUrlDataCleanupJob.class).build());
    }

    private String getDatabaseUrl() {
        return this.databaseProtocol + this.databaseHost + ":" + this.databasePort + "/" + this.databaseName;
    }

    private Builder abstractSchedulingConfigBuilder() {
        return SchedulingConfigProperties.newBuilder().withJobClass(ResponseDataCleanupJob.class)
                .withThreadCountKey(KEY_CLEANUP_JOB_THREAD_COUNT).withCronExpressionKey(KEY_CLEANUP_JOB_CRON_EXPRESSION)
                .withJobStoreDbUrl(this.getDatabaseUrl()).withJobStoreDbUsername(this.databaseUsername)
                .withJobStoreDbPassword(this.databasePassword).withJobStoreDbDriver(this.databaseDriver);
    }
}
