/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.scheduling;

import java.util.Arrays;
import java.util.HashSet;

import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for Quartz {@link Scheduler} instance with some convenient
 * functions for creating scheduled jobs. Use
 * {@link AbstractOsgpSchedulerConfig} to create a Quartz {@link Scheduler}
 * instance which can be set using
 * {@link OsgpScheduler#OsgpScheduler(Scheduler)}.
 */
public class OsgpScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpScheduler.class);

    private final Scheduler quartzScheduler;

    /**
     * Set a Quartz {@link Scheduler} instance.
     *
     * @param quartzScheduler
     *            A Quartz {@link Scheduler} instance.
     *
     * @throws SchedulerException
     *             If the Quartz {@link Scheduler} can't be created or started.
     */
    public OsgpScheduler(final Scheduler quartzScheduler) throws SchedulerException {
        this.quartzScheduler = quartzScheduler;
        LOGGER.info("Starting {}.", quartzScheduler.getSchedulerName());
    }

    /**
     * Shutdown the Quartz {@link Scheduler} and delete the data.
     *
     * @throws SchedulerException
     *             If the Quartz {@link Scheduler} can't shutdown or the data
     *             can't be deleted.
     */
    public void shutdown() throws SchedulerException {
        LOGGER.info("Stopping {}.", this.quartzScheduler.getSchedulerName());
        this.quartzScheduler.shutdown(true);
        this.quartzScheduler.clear();
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
     *
     * @throws SchedulerException
     *             In case the scheduler has been shutdown.
     */
    public String getQuartzSchedulerName() throws SchedulerException {
        return this.quartzScheduler.getSchedulerName();
    }

    /**
     * Create a {@link JobDetail} instance using a class which defines the
     * actions for a scheduled job.
     *
     * @param jobClass
     *            The class which defines the actions for a scheduled job.
     *
     * @return A {@link JobDetail} instance.
     */
    public JobDetail createJobDetail(final Class<? extends Job> jobClass) {
        return JobBuilder.newJob().ofType(jobClass).storeDurably().withIdentity(jobClass.getSimpleName()).build();
    }

    /**
     * Create a {@link Trigger} instance using a {@link JobDetail} instance and
     * a CRON-expression.
     *
     * @param jobDetail
     *            A {@link JobDetail} instance, possibly created using
     *            {@link OsgpScheduler#createJobDetail(Class)}.
     * @param cronExpression
     *            A CRON-expression.
     *
     * @return A {@link Trigger} instance.
     */
    public Trigger createJobTrigger(final JobDetail jobDetail, final String cronExpression) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName() + "-Trigger")
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

    /**
     * Convenience method for creating a job and trigger, then adding and
     * scheduling the job using the trigger. This method uses
     * {@link OsgpScheduler#createJobDetail(Class)} and
     * {@link OsgpScheduler#createJobTrigger(JobDetail, String)}.
     *
     * @param jobClass
     *            The class which defines the actions of the scheduled job.
     * @param cronExpression
     *            The input for the trigger, a Quartz CRON expression like
     *            {@code 0 0/1 * * * ?} for example.
     *
     * @throws SchedulerException
     *             In case adding or scheduling of the job fails.
     */
    public void createAndScheduleJob(final Class<? extends Job> jobClass, final String cronExpression)
            throws SchedulerException {

        LOGGER.info("Scheduling job: {} using Quartz cron expression: {}", jobClass.getSimpleName(), cronExpression);

        // Create job and trigger.
        final JobDetail jobDetail = this.createJobDetail(jobClass);
        final Trigger trigger = this.createJobTrigger(jobDetail, cronExpression);

        // Add and schedule for trigger.
        this.quartzScheduler.addJob(jobDetail, true);
        this.quartzScheduler.scheduleJob(jobDetail, new HashSet<>(Arrays.asList(trigger)), true);
    }

    public void deleteScheduledJob(final Class<? extends Job> jobClass) throws SchedulerException {
        final JobDetail jobDetail = this.createJobDetail(jobClass);
        this.quartzScheduler.deleteJob(jobDetail.getKey());
    }
}
