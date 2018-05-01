/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.application.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.PostgreSQLDelegate;
import org.quartz.simpl.SimpleThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * This class provides the basic components used for task scheduling.
 */
public abstract class AbstractSchedulingConfig extends AbstractConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        final AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(this.applicationContext);
        return jobFactory;
    }

    /**
     * Construct the scheduler taskpool with specified job and trigger
     *
     * @param schedulingConfigProperties
     *            an object containing all the properties needed to configure
     *            the Quartz scheduler instance
     * @return the Quartz scheduler instance
     * @throws SchedulerException
     *             when issues occur in constructing schedules
     */
    protected Scheduler constructScheduler(final SchedulingConfigProperties schedulingConfigProperties)
            throws SchedulerException {

        final Properties properties = this.constructQuartzConfiguration(schedulingConfigProperties);

        final StdSchedulerFactory factory = new StdSchedulerFactory();
        factory.initialize(properties);
        final Scheduler scheduler = factory.getScheduler();
        scheduler.setJobFactory(this.springBeanJobFactory());

        final JobDetail jobDetail = this.createJobDetail(schedulingConfigProperties.getJobClass());
        scheduler.addJob(jobDetail, true);

        final Trigger trigger = this.createJobTrigger(jobDetail,
                this.environment.getRequiredProperty(schedulingConfigProperties.getCronExpressionKey()));
        scheduler.scheduleJob(jobDetail, new HashSet<>(Arrays.asList(trigger)), true);

        scheduler.start();

        return scheduler;
    }

    private Properties constructQuartzConfiguration(final SchedulingConfigProperties schedulingConfigProperties) {
        final Properties properties = new Properties();

        // Default Properties
        properties.put("org.quartz.scheduler.instanceName", schedulingConfigProperties.getJobClass().getSimpleName());
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        properties.put("org.quartz.scheduler.rmi.export", Boolean.FALSE.toString());
        properties.put("org.quartz.scheduler.rmi.proxy", Boolean.FALSE.toString());
        properties.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", Boolean.FALSE.toString());
        properties.put("org.quartz.scheduler.makeSchedulerThreadDaemon", Boolean.TRUE.toString());
        properties.put("org.quartz.scheduler.interruptJobsOnShutdown", Boolean.TRUE.toString());

        properties.put("org.quartz.threadPool.class", SimpleThreadPool.class.getName());
        properties.put("org.quartz.threadPool.threadCount",
                this.environment.getRequiredProperty(schedulingConfigProperties.getThreadCountKey()));
        properties.put("org.quartz.threadPool.makeThreadsDaemons", Boolean.TRUE.toString());
        properties.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread",
                Boolean.TRUE.toString());

        // Configure shutdown behavior
        properties.put("org.quartz.plugin.shutdownhook.class",
                org.quartz.plugins.management.ShutdownHookPlugin.class.getName());
        properties.put("org.quartz.plugin.shutdownhook.cleanShutdown", Boolean.TRUE.toString());

        // JobStore specific configuration
        properties.put("org.quartz.jobStore.class", JobStoreTX.class.getName());
        properties.put("org.quartz.jobStore.driverDelegateClass", PostgreSQLDelegate.class.getName());
        properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.put("org.quartz.jobStore.useProperties", Boolean.TRUE.toString());
        properties.put("org.quartz.jobStore.isClustered", Boolean.TRUE.toString());
        properties.put("org.quartz.jobStore.misfireThreshold", "60000");
        properties.put("org.quartz.jobStore.dataSource", "quartzDefault");

        properties.put("org.quartz.dataSource.quartzDefault.driver", schedulingConfigProperties.getJobStoreDbDriver());
        properties.put("org.quartz.dataSource.quartzDefault.URL", schedulingConfigProperties.getJobStoreDbUrl());
        properties.put("org.quartz.dataSource.quartzDefault.user", schedulingConfigProperties.getJobStoreDbUsername());
        properties.put("org.quartz.dataSource.quartzDefault.password",
                schedulingConfigProperties.getJobStoreDbPassword());
        properties.put("org.quartz.dataSource.quartzDefault.provider", "hikaricp");

        return properties;
    }

    private JobDetail createJobDetail(final Class<? extends Job> jobClass) {
        return JobBuilder.newJob().ofType(jobClass).storeDurably().withIdentity(jobClass.getSimpleName()).build();
    }

    private Trigger createJobTrigger(final JobDetail jobDetail, final String cronExpression) {
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName() + "-Trigger")
                .forJob(jobDetail).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
    }
}
