// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.scheduling;

import static org.quartz.DateBuilder.futureDate;

import com.google.common.collect.Sets;
import java.util.TimeZone;
import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for Quartz {@link Scheduler} instance with some convenient functions for creating
 * scheduled jobs. Use {@link AbstractOsgpSchedulerConfig} to create a Quartz {@link Scheduler}
 * instance which can be set using {@link OsgpScheduler#OsgpScheduler(Scheduler)}.
 */
public class OsgpScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsgpScheduler.class);

  private final Scheduler quartzScheduler;

  /**
   * Set a Quartz {@link Scheduler} instance.
   *
   * @param quartzScheduler A Quartz {@link Scheduler} instance.
   * @throws SchedulerException If the Quartz {@link Scheduler} can't be created or started.
   */
  public OsgpScheduler(final Scheduler quartzScheduler) throws SchedulerException {
    this.quartzScheduler = quartzScheduler;
    LOGGER.info("Starting {}.", quartzScheduler.getSchedulerName());
  }

  /**
   * Clears all the data from the scheduler.
   *
   * @throws SchedulerException If the Quartz {@link Scheduler} can't clear the data.
   */
  public void clear() throws SchedulerException {
    // Clear existing jobs, to make sure the latest version is loaded
    // and deleted triggers are removed from the database
    LOGGER.info("Clear scheduler {}.", this.quartzScheduler.getSchedulerName());
    this.quartzScheduler.clear();
  }

  /**
   * Shutdown the Quartz {@link Scheduler}
   *
   * @throws SchedulerException If the Quartz {@link Scheduler} can't shutdown
   */
  public void shutdown() throws SchedulerException {
    LOGGER.info("Stopping {}.", this.quartzScheduler.getSchedulerName());
    this.quartzScheduler.shutdown(true);
  }

  /**
   * Get the Quartz {@link Scheduler} instance.
   *
   * @return The Quartz {@link Scheduler} instance.
   */
  public Scheduler getQuartzScheduler() {
    return this.quartzScheduler;
  }

  /**
   * Get the name of the Quartz {@link Scheduler} instance.
   *
   * @return The name of the Quartz {@link Scheduler} instance.
   * @throws SchedulerException In case the scheduler has been shutdown.
   */
  public String getQuartzSchedulerName() throws SchedulerException {
    return this.quartzScheduler.getSchedulerName();
  }

  /**
   * Create a {@link JobDetail} instance using a class which defines the actions for a scheduled
   * job.
   *
   * @param jobClass The class which defines the actions for a scheduled job.
   * @return A {@link JobDetail} instance.
   */
  public JobDetail createJobDetail(final Class<? extends Job> jobClass) {
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
   *     OsgpScheduler#createJobDetail(Class)}.
   * @param cronExpression A CRON-expression.
   * @param timeZone A Timezone for the CRON-expression.
   * @param jobDataMap A map containing data that can be used by job.
   * @return A {@link Trigger} instance.
   */
  public Trigger createJobTrigger(
      final JobDetail jobDetail,
      final String cronExpression,
      final TimeZone timeZone,
      final JobDataMap jobDataMap) {
    return TriggerBuilder.newTrigger()
        .forJob(jobDetail)
        .withIdentity(this.getTriggerIdentity(jobDetail))
        .usingJobData(jobDataMap)
        .forJob(jobDetail)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).inTimeZone(timeZone))
        .build();
  }

  /**
   * Create a {@link Trigger} instance using a {@link JobDetail} instance and a delay
   *
   * @param jobDetail A {@link JobDetail} instance, possibly created using {@link
   *     OsgpScheduler#createJobDetail(Class)}.
   * @param interval Interval of the trigger.
   * @param intervalUnit Interval unit.
   * @param jobDataMap A map containing data that can be used by job.
   * @return A {@link Trigger} instance.
   */
  public Trigger createJobTrigger(
      final JobDetail jobDetail,
      final Integer interval,
      final IntervalUnit intervalUnit,
      final JobDataMap jobDataMap) {
    return TriggerBuilder.newTrigger()
        .forJob(jobDetail)
        .withIdentity(this.getTriggerIdentity(jobDetail))
        .startAt(futureDate(interval, intervalUnit))
        .usingJobData(jobDataMap)
        .forJob(jobDetail)
        .build();
  }

  public TriggerKey getTriggerKey(final Class<? extends Job> jobClass) {
    final JobDetail jobDetail = this.createJobDetail(jobClass);
    return TriggerKey.triggerKey(this.getTriggerIdentity(jobDetail));
  }

  /**
   * Create a {@link Trigger} instance using a {@link JobDetail} instance and a CRON-expression, the
   * default TimeZone will be used
   *
   * @param jobDetail A {@link JobDetail} instance, possibly created using {@link
   *     OsgpScheduler#createJobDetail(Class)}.
   * @param cronExpression A CRON-expression.
   * @return A {@link Trigger} instance.
   */
  public Trigger createJobTrigger(final JobDetail jobDetail, final String cronExpression) {
    return this.createJobTrigger(
        jobDetail, cronExpression, TimeZone.getTimeZone("UTC"), new JobDataMap());
  }

  /**
   * Convenience method for creating a job and trigger, then adding and scheduling the job using the
   * trigger. This method uses {@link OsgpScheduler#createJobDetail(Class)} and {@link
   * OsgpScheduler#createJobTrigger(JobDetail, String)}.
   *
   * @param jobClass The class which defines the actions of the scheduled job.
   * @param cronExpression The input for the trigger, a Quartz CRON expression like {@code 0 0/1 * *
   *     * ?} for example.
   * @param timeZone A Timezone for the CRON-expression.
   * @throws SchedulerException In case adding or scheduling of the job fails.
   */
  public void createAndScheduleJob(
      final Class<? extends Job> jobClass, final String cronExpression, final TimeZone timeZone)
      throws SchedulerException {
    this.createAndScheduleJob(jobClass, cronExpression, timeZone, new JobDataMap());
  }

  /**
   * Convenience method for creating a job and trigger, then adding and scheduling the job using the
   * trigger. This method uses {@link OsgpScheduler#createJobDetail(Class)} and {@link
   * OsgpScheduler#createJobTrigger(JobDetail, String)}.
   *
   * @param jobClass The class which defines the actions of the scheduled job.
   * @param interval Interval of the trigger.
   * @param intervalUnit Interval unit.
   * @throws SchedulerException In case adding or scheduling of the job fails.
   */
  public void createAndScheduleJob(
      final Class<? extends Job> jobClass,
      final Integer interval,
      final IntervalUnit intervalUnit,
      final JobDataMap jobDataMap)
      throws SchedulerException {

    LOGGER.info(
        "Scheduling job: {} using Quartz delay of: {} {}",
        jobClass.getSimpleName(),
        interval,
        intervalUnit);

    // Create job and trigger.
    final JobDetail jobDetail = this.createJobDetail(jobClass);
    final Trigger trigger = this.createJobTrigger(jobDetail, interval, intervalUnit, jobDataMap);

    // Add and schedule for trigger.
    this.addAndScheduleJob(jobDetail, trigger);
  }

  /**
   * Convenience method for creating a job and trigger, then adding and scheduling the job using the
   * trigger. This method uses {@link OsgpScheduler#createJobDetail(Class)} and {@link
   * OsgpScheduler#createJobTrigger(JobDetail, String)}.
   *
   * @param jobClass The class which defines the actions of the scheduled job.
   * @param cronExpression The input for the trigger, a Quartz CRON expression like {@code 0 0/1 * *
   *     * ?} for example.
   * @param timeZone A Timezone for the CRON-expression.
   * @param jobDataMap A map containing data that can be used by job.
   * @throws SchedulerException In case adding or scheduling of the job fails.
   */
  public void createAndScheduleJob(
      final Class<? extends Job> jobClass,
      final String cronExpression,
      final TimeZone timeZone,
      final JobDataMap jobDataMap)
      throws SchedulerException {

    LOGGER.info(
        "Scheduling job: {} using Quartz cron expression: {}",
        jobClass.getSimpleName(),
        cronExpression);

    // Create job and trigger.
    final JobDetail jobDetail = this.createJobDetail(jobClass);
    final Trigger trigger = this.createJobTrigger(jobDetail, cronExpression, timeZone, jobDataMap);

    // Add and schedule for trigger.
    this.addAndScheduleJob(jobDetail, trigger);
  }

  private void addAndScheduleJob(final JobDetail jobDetail, final Trigger trigger)
      throws SchedulerException {
    this.quartzScheduler.addJob(jobDetail, true);
    this.quartzScheduler.scheduleJob(jobDetail, Sets.newHashSet(trigger), true);
  }

  /**
   * Convenience method for creating a job and trigger, then adding and scheduling the job using the
   * trigger. This method uses {@link OsgpScheduler#createJobDetail(Class)} and {@link
   * OsgpScheduler#createJobTrigger(JobDetail, String)}.
   *
   * @param jobClass The class which defines the actions of the scheduled job.
   * @param cronExpression The input for the trigger, a Quartz CRON expression like {@code 0 0/1 * *
   *     * ?} for example.
   * @throws SchedulerException In case adding or scheduling of the job fails.
   */
  public void createAndScheduleJob(final Class<? extends Job> jobClass, final String cronExpression)
      throws SchedulerException {
    this.createAndScheduleJob(jobClass, cronExpression, TimeZone.getTimeZone("UTC"));
  }

  public void deleteScheduledJob(final Class<? extends Job> jobClass) throws SchedulerException {
    final JobDetail jobDetail = this.createJobDetail(jobClass);

    LOGGER.info("Delete job: {}", jobClass.getSimpleName());

    this.quartzScheduler.deleteJob(jobDetail.getKey());
  }

  private String getTriggerIdentity(final JobDetail jobDetail) {
    return jobDetail.getKey().getName() + "-Trigger";
  }
}
