/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.scheduling;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Set;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@ExtendWith(MockitoExtension.class)
class OsgpSchedulerTest {

  private static final String CRON_EXPRESSION = "0 0/5 * * * ?";

  @Spy private Scheduler quartzScheduler;

  @InjectMocks private OsgpScheduler osgpScheduler;
  @Captor ArgumentCaptor<JobDetail> jobDetailArgumentCaptor;
  @Captor ArgumentCaptor<JobDetail> jobDetail2ArgumentCaptor;
  @Captor ArgumentCaptor<Set<? extends Trigger>> jobTriggerSetArgumentCaptor;

  @Test
  void testClear() throws SchedulerException {
    this.osgpScheduler.clear();
    verify(this.quartzScheduler).clear();
  }

  @Test
  void testShutdown() throws SchedulerException {
    this.osgpScheduler.shutdown();
    verify(this.quartzScheduler).shutdown(true);
    // Clearing on shutdown causes issues when deploying in Kubernetes
    verify(this.quartzScheduler, never()).clear();
  }

  @Test
  void testCreateAndScheduleJob2Args() throws SchedulerException {
    this.osgpScheduler.createAndScheduleJob(TestJob.class, CRON_EXPRESSION);

    this.assertQuartzTrigger(TestJob.class, CRON_EXPRESSION, DateTimeZone.UTC.toTimeZone());
  }

  @Test
  void testCreateAndScheduleJobTimeZone() throws SchedulerException {
    final TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");
    this.osgpScheduler.createAndScheduleJob(TestJob.class, CRON_EXPRESSION, timeZone);

    this.assertQuartzTrigger(TestJob.class, CRON_EXPRESSION, timeZone);
  }

  @Test
  void testCreateAndScheduleJobDataMap() throws SchedulerException {
    final JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("A", 1L);
    final TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");
    this.osgpScheduler.createAndScheduleJob(TestJob.class, CRON_EXPRESSION, timeZone, jobDataMap);

    final CronTrigger cronTrigger =
        this.assertQuartzTrigger(TestJob.class, CRON_EXPRESSION, timeZone);

    assertThat(cronTrigger.getJobDataMap().size()).isOne();
    assertThat(cronTrigger.getJobDataMap().get("A")).isEqualTo(1L);
  }

  @Test
  void testGetTriggerKey() throws SchedulerException {
    final TriggerKey triggerKey = this.osgpScheduler.getTriggerKey(TestJob.class);
    assertThat(triggerKey.getName()).isEqualTo(TestJob.class.getSimpleName() + "-Trigger");
    assertThat(triggerKey.getGroup()).isEqualTo("DEFAULT");
  }

  CronTrigger assertQuartzTrigger(
      final Class<? extends Job> jobClazz, final String cronExpression, final TimeZone timeZone)
      throws SchedulerException {

    verify(this.quartzScheduler).addJob(this.jobDetailArgumentCaptor.capture(), eq(true));
    assertThat(this.jobDetailArgumentCaptor.getValue().getKey().getName())
        .isEqualTo(jobClazz.getSimpleName());
    verify(this.quartzScheduler)
        .scheduleJob(
            this.jobDetail2ArgumentCaptor.capture(),
            this.jobTriggerSetArgumentCaptor.capture(),
            eq(true));
    assertThat(this.jobDetail2ArgumentCaptor.getValue())
        .isEqualTo(this.jobDetailArgumentCaptor.getValue());
    assertThat(this.jobTriggerSetArgumentCaptor.getValue().size()).isOne();
    final CronTrigger cronTrigger =
        (CronTrigger) this.jobTriggerSetArgumentCaptor.getValue().iterator().next();
    assertThat(cronTrigger.getCronExpression()).isEqualTo(cronExpression);
    assertThat(cronTrigger.getTimeZone()).isEqualTo(timeZone);

    return cronTrigger;
  }
}
