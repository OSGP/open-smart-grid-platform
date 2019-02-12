/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package org.opensmartgridplatform.shared.application.config;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.util.StringUtils;

/**
 * This class provides the basic components used for task scheduling.
 */
public abstract class AbstractSchedulingConfig extends AbstractConfig {

    protected static final String KEY_QUARTZ_SCHEDULER_THREAD_COUNT = "quartz.scheduler.thread.count";

    @Value("${db.driver}")
    protected String databaseDriver;

    @Value("${db.password}")
    protected String databasePassword;

    @Value("${db.protocol}")
    protected String databaseProtocol;

    @Value("${db.host}")
    protected String databaseHost;

    @Value("${db.port}")
    protected String databasePort;

    @Value("${db.name}")
    protected String databaseName;

    @Value("${db.username}")
    protected String databaseUsername;

    @Autowired
    protected ApplicationContext applicationContext;

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

        final Scheduler scheduler = this.constructBareScheduler(schedulingConfigProperties);

        final JobDetail jobDetail = this.createJobDetail(schedulingConfigProperties.getJobClass());
        scheduler.addJob(jobDetail, true);

        final Trigger trigger = this.createJobTrigger(jobDetail,
                this.environment.getRequiredProperty(schedulingConfigProperties.getCronExpressionKey()));
        scheduler.scheduleJob(jobDetail, new HashSet<>(Arrays.asList(trigger)), true);

        scheduler.start();

        return scheduler;
    }

    protected Scheduler constructBareScheduler(final SchedulingConfigProperties schedulingConfigProperties)
            throws SchedulerException {

        final Properties properties = this.constructQuartzConfiguration(schedulingConfigProperties);

        final StdSchedulerFactory factory = new StdSchedulerFactory();
        factory.initialize(properties);
        final Scheduler scheduler = factory.getScheduler();
        scheduler.setJobFactory(this.springBeanJobFactory());

        return scheduler;
    }

    private Properties constructQuartzConfiguration(final SchedulingConfigProperties schedulingConfigProperties) {
        final Properties properties = new Properties();

        // Default Properties
        if (StringUtils.isEmpty(schedulingConfigProperties.getSchedulerName())) {
            properties.put("org.quartz.scheduler.instanceName",
                    schedulingConfigProperties.getJobClass().getSimpleName());
        } else {
            properties.put("org.quartz.scheduler.instanceName", schedulingConfigProperties.getSchedulerName());
        }
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        properties.put("org.quartz.scheduler.rmi.export", Boolean.FALSE.toString());
        properties.put("org.quartz.scheduler.rmi.proxy", Boolean.FALSE.toString());
        properties.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", Boolean.FALSE.toString());
        properties.put("org.quartz.scheduler.makeSchedulerThreadDaemon", Boolean.TRUE.toString());
        properties.put("org.quartz.scheduler.interruptJobsOnShutdown", Boolean.FALSE.toString());

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
        properties.put("org.quartz.dataSource.quartzDefault.maxConnections",
                schedulingConfigProperties.getMaxConnections());

        return properties;
    }

    protected JobDetail createJobDetail(final Class<? extends Job> jobClass) {
        return JobBuilder.newJob().ofType(jobClass).storeDurably().withIdentity(jobClass.getSimpleName()).build();
    }

    protected Trigger createJobTrigger(final JobDetail jobDetail, final String cronExpression) {
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName() + "-Trigger")
                .forJob(jobDetail).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
    }

    /**
     * Convenience method for creating a job and trigger, then adding and
     * scheduling the job using the trigger. This method uses
     * {@link AbstractSchedulingConfig#createJobDetail(Class)} and
     * {@link AbstractSchedulingConfig#createJobTrigger(JobDetail, String)}.
     *
     * @param quartzScheduler
     *            An instance of {@link Scheduler}.
     * @param jobClass
     *            The class which defines the actions of the scheduled job.
     * @param cronExpression
     *            The input for the trigger, a Quartz CRON expression like
     *            {@code 0 0/1 * * * ?} for example.
     *
     * @throws SchedulerException
     *             In case adding or scheduling of the job fails.
     */
    protected void createAndScheduleJob(final Scheduler quartzScheduler, final Class<? extends Job> jobClass,
            final String cronExpression) throws SchedulerException {
        // Create job and trigger.
        final JobDetail jobDetail = this.createJobDetail(jobClass);
        final Trigger trigger = this.createJobTrigger(jobDetail, cronExpression);

        // Add and schedule for trigger.
        quartzScheduler.addJob(jobDetail, true);
        quartzScheduler.scheduleJob(jobDetail, new HashSet<>(Arrays.asList(trigger)), true);
    }

    protected String getDatabaseUrl() {
        return this.databaseProtocol + this.databaseHost + ":" + this.databasePort + "/" + this.databaseName;
    }
}
