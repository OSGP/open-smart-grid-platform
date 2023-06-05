// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
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

/** This class provides the basic configuration used for Quartz schedulers. */
public abstract class AbstractSchedulingConfig extends AbstractConfig {

  protected static final String KEY_QUARTZ_SCHEDULER_THREAD_COUNT = "quartz.scheduler.thread.count";

  @Value("${db.driver:#{null}}")
  protected String databaseDriver;

  @Value("${db.password:#{null}}")
  protected String databasePassword;

  @Value("${db.protocol:#{null}}")
  protected String databaseProtocol;

  @Value("${db.host:#{null}}")
  protected String databaseHost;

  @Value("${db.port:#{null}}")
  protected String databasePort;

  @Value("${db.name:#{null}}")
  protected String databaseName;

  @Value("${db.username:#{null}}")
  protected String databaseUsername;

  @Autowired private ApplicationContext applicationContext;

  @Bean
  public SpringBeanJobFactory springBeanJobFactory() {
    final AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(this.applicationContext);
    return jobFactory;
  }

  /**
   * Construct the scheduler task-pool with specified job and trigger.
   *
   * @param schedulingConfigProperties Object containing all the properties needed to configure the
   *     Quartz scheduler instance.
   * @return The Quartz scheduler instance.
   * @throws SchedulerException When issues occur in constructing schedules.
   * @deprecated Use {@link
   *     AbstractSchedulingConfig#constructAndStartQuartzScheduler(SchedulingConfigProperties)}.
   */
  @Deprecated
  protected Scheduler constructScheduler(
      final SchedulingConfigProperties schedulingConfigProperties) throws SchedulerException {

    final Scheduler scheduler = this.constructAndStartQuartzScheduler(schedulingConfigProperties);

    final JobDetail jobDetail = this.createJobDetail(schedulingConfigProperties.getJobClass());
    scheduler.addJob(jobDetail, true);

    final Trigger trigger =
        this.createJobTrigger(
            jobDetail,
            this.environment.getRequiredProperty(
                schedulingConfigProperties.getCronExpressionKey()));
    scheduler.scheduleJob(jobDetail, new HashSet<>(Arrays.asList(trigger)), true);

    return scheduler;
  }

  /**
   * Construct and start the Quartz scheduler instance. Use
   * {@link OsgpScheduler#createAndScheduleJob(Scheduler, Class, String) to
   * add scheduled jobs to the Quartz scheduler.
   *
   * @param schedulingConfigProperties
   *            an object containing all the properties needed to configure
   *            the Quartz scheduler instance
   *
   * @return The Quartz scheduler instance.
   *
   * @throws SchedulerException
   *             If construction of the scheduler or starting the scheduler
   *             fails.
   */
  protected Scheduler constructAndStartQuartzScheduler(
      final SchedulingConfigProperties schedulingConfigProperties) throws SchedulerException {

    final Scheduler scheduler = this.constructSchedulerInstance(schedulingConfigProperties);
    scheduler.start();

    return scheduler;
  }

  /**
   * Construct and delay starting the Quartz scheduler instance. Use
   * {@link OsgpScheduler#createAndScheduleJob(Scheduler, Class, String) to
   * add scheduled jobs to the Quartz scheduler.
   *
   * @param schedulingConfigProperties
   *            an object containing all the properties needed to configure
   *            the Quartz scheduler instance
   * @param startDelayInSeconds
   *            The startup delay in seconds.
   *
   * @return The Quartz scheduler instance.
   *
   * @throws SchedulerException
   *             If construction of the scheduler or starting the scheduler
   *             fails.
   */
  protected Scheduler constructAndDelayStartQuartzScheduler(
      final SchedulingConfigProperties schedulingConfigProperties, final int startDelayInSeconds)
      throws SchedulerException {

    final Scheduler scheduler = this.constructSchedulerInstance(schedulingConfigProperties);
    scheduler.startDelayed(startDelayInSeconds);

    return scheduler;
  }

  private Scheduler constructSchedulerInstance(
      final SchedulingConfigProperties schedulingConfigProperties) throws SchedulerException {

    final Properties properties = this.constructQuartzConfiguration(schedulingConfigProperties);

    final StdSchedulerFactory factory = new StdSchedulerFactory();
    factory.initialize(properties);
    final Scheduler scheduler = factory.getScheduler();
    scheduler.setJobFactory(this.springBeanJobFactory());

    return scheduler;
  }

  private Properties constructQuartzConfiguration(
      final SchedulingConfigProperties schedulingConfigProperties) {
    final Properties properties = new Properties();

    // Default Properties
    if (StringUtils.hasText(schedulingConfigProperties.getSchedulerName())) {
      properties.put(
          "org.quartz.scheduler.instanceName", schedulingConfigProperties.getSchedulerName());
    } else {
      properties.put(
          "org.quartz.scheduler.instanceName",
          schedulingConfigProperties.getJobClass().getSimpleName());
    }
    properties.put("org.quartz.scheduler.instanceId", "AUTO");
    properties.put("org.quartz.scheduler.rmi.export", Boolean.FALSE.toString());
    properties.put("org.quartz.scheduler.rmi.proxy", Boolean.FALSE.toString());
    properties.put(
        "org.quartz.scheduler.wrapJobExecutionInUserTransaction", Boolean.FALSE.toString());
    properties.put("org.quartz.scheduler.makeSchedulerThreadDaemon", Boolean.TRUE.toString());
    properties.put("org.quartz.scheduler.interruptJobsOnShutdown", Boolean.FALSE.toString());

    properties.put("org.quartz.threadPool.class", SimpleThreadPool.class.getName());
    properties.put(
        "org.quartz.threadPool.threadCount",
        this.environment.getRequiredProperty(schedulingConfigProperties.getThreadCountKey()));
    properties.put("org.quartz.threadPool.makeThreadsDaemons", Boolean.TRUE.toString());
    properties.put(
        "org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread",
        Boolean.TRUE.toString());

    // Configure shutdown behavior
    properties.put(
        "org.quartz.plugin.shutdownhook.class",
        org.quartz.plugins.management.ShutdownHookPlugin.class.getName());
    properties.put("org.quartz.plugin.shutdownhook.cleanShutdown", Boolean.TRUE.toString());

    // JobStore specific configuration
    properties.put("org.quartz.jobStore.class", JobStoreTX.class.getName());
    properties.put("org.quartz.jobStore.driverDelegateClass", PostgreSQLDelegate.class.getName());
    properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
    properties.put(
        "org.quartz.jobStore.useProperties", schedulingConfigProperties.isUseProperties());
    properties.put("org.quartz.jobStore.isClustered", Boolean.TRUE.toString());
    properties.put("org.quartz.jobStore.misfireThreshold", "60000");
    properties.put("org.quartz.jobStore.dataSource", "quartzDefault");

    if (StringUtils.hasText(schedulingConfigProperties.getJobStoreDbDriver())) {
      properties.put(
          "org.quartz.dataSource.quartzDefault.driver",
          schedulingConfigProperties.getJobStoreDbDriver());
      properties.put(
          "org.quartz.dataSource.quartzDefault.URL", schedulingConfigProperties.getJobStoreDbUrl());
      properties.put(
          "org.quartz.dataSource.quartzDefault.user",
          schedulingConfigProperties.getJobStoreDbUsername());
      properties.put(
          "org.quartz.dataSource.quartzDefault.password",
          schedulingConfigProperties.getJobStoreDbPassword());
    } else {
      throw new IllegalArgumentException("Scheduling datasource not properly configured");
    }

    properties.put("org.quartz.dataSource.quartzDefault.provider", "hikaricp");
    properties.put(
        "org.quartz.dataSource.quartzDefault.maxConnections",
        schedulingConfigProperties.getMaxConnections());

    return properties;
  }

  /**
   * Create a {@link JobDetail} instance using a class which defines the actions for a scheduled
   * job.
   *
   * @param jobClass The class which defines the actions for a scheduled job.
   * @return A {@link JobDetail} instance.
   * @deprecated Use {@link OsgpScheduler#createJobDetail(Class)}.
   */
  @Deprecated
  private JobDetail createJobDetail(final Class<? extends Job> jobClass) {
    return JobBuilder.newJob()
        .ofType(jobClass)
        .storeDurably()
        .withIdentity(jobClass.getSimpleName())
        .build();
  }

  /**
   * Create a {@link Trigger} instance using a {@link JobDetail} instance and a CRON-expression.
   *
   * @param jobDetail A {@link JobDetail} instance, possibly created using {@link
   *     AbstractSchedulingConfig#createJobDetail(Class)}.
   * @param cronExpression A CRON-expression.
   * @return A {@link Trigger} instance.
   * @deprecated Use {@link OsgpScheduler#createJobTrigger(JobDetail, String)}.
   */
  @Deprecated
  private Trigger createJobTrigger(final JobDetail jobDetail, final String cronExpression) {
    return TriggerBuilder.newTrigger()
        .forJob(jobDetail)
        .withIdentity(jobDetail.getKey().getName() + "-Trigger")
        .forJob(jobDetail)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
        .build();
  }

  protected String getDatabaseUrl() {
    return this.databaseProtocol
        + this.databaseHost
        + ":"
        + this.databasePort
        + "/"
        + this.databaseName;
  }
}
