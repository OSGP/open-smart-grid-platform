/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.adapter.ws.microgrids.application.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alliander.osgp.adapter.ws.microgrids.application.services.RtuResponseDataCleanupJob;
import com.alliander.osgp.shared.application.config.AbstractSchedulingConfig;

@EnableScheduling
@Configuration
public class SchedulingConfig extends AbstractSchedulingConfig {

    private static final String KEY_CLEANUP_JOB_CRON_EXPRESSION = "scheduling.job.cleanup.cron.expression";
    private static final String KEY_CLEANUP_JOB_THREAD_COUNT = "scheduling.job.cleanup.thread.count";

    // @Value("${scheduling.task.scheduler.thread.name.prefix}")
    // private String prefix;

    @Value("${db.driver}")
    private String databaseDriver;

    @Value("${db.password}")
    private String databasePassword;

    @Value("${db.protocol}")
    private String databaseProtocol; // =jdbc:postgresql://

    @Value("${db.host}")
    private String databaseHost; // =localhost

    @Value("${db.port}")
    private String databasePort; // =5432

    @Value("${db.name}")
    private String databaseName; // =osgp_adapter_ws_smartmetering

    @Value("${db.username}")
    private String databaseUsername;

    @Value("${scheduling.cleanupjob.retention.time.in.days}")
    private int cleanupJobRetentionTimeInDays;

    @Bean
    public int cleanupJobRetentionTimeInDays() {
        return this.cleanupJobRetentionTimeInDays;
    }

    @Bean(destroyMethod = "shutdown")
    public Scheduler cleanupJobScheduler() throws SchedulerException {
        return this.constructScheduler(RtuResponseDataCleanupJob.class, KEY_CLEANUP_JOB_THREAD_COUNT,
                KEY_CLEANUP_JOB_CRON_EXPRESSION, this.getDatabaseUrl(), this.databaseUsername, this.databasePassword,
                this.databaseDriver);
    }

    private String getDatabaseUrl() {
        return this.databaseProtocol + this.databaseHost + ":" + this.databasePort + "/" + this.databaseName;
    }
}
