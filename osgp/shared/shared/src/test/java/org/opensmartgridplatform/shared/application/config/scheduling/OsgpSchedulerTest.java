//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.config.scheduling;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Set;
import java.util.TimeZone;
import org.joda.time.DateTime;
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
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
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

    final CronTrigger cronTrigger = (CronTrigger) this.assertJobDetail(TestJob.class);
    this.assertCronTrigger(cronTrigger, CRON_EXPRESSION, DateTimeZone.UTC.toTimeZone());
  }

  @Test
  void testCreateAndScheduleJobTimeZone() throws SchedulerException {
    final TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");
    this.osgpScheduler.createAndScheduleJob(TestJob.class, CRON_EXPRESSION, timeZone);

    final CronTrigger cronTrigger = (CronTrigger) this.assertJobDetail(TestJob.class);
    this.assertCronTrigger(cronTrigger, CRON_EXPRESSION, timeZone);
  }

  @Test
  void testCreateAndScheduleJobDataMap() throws SchedulerException {
    final JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("A", 1L);
    final TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");
    this.osgpScheduler.createAndScheduleJob(TestJob.class, CRON_EXPRESSION, timeZone, jobDataMap);

    final CronTrigger cronTrigger = (CronTrigger) this.assertJobDetail(TestJob.class);
    this.assertCronTrigger(cronTrigger, CRON_EXPRESSION, timeZone);

    assertThat(cronTrigger.getJobDataMap().size()).isOne();
    assertThat(cronTrigger.getJobDataMap().get("A")).isEqualTo(1L);
  }

  @Test
  void testCreateAndScheduleSimpleJobDataMap() throws SchedulerException {
    final JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("A", 1L);
    this.osgpScheduler.createAndScheduleJob(TestJob.class, 40, IntervalUnit.SECOND, jobDataMap);

    final SimpleTrigger simpleTrigger = (SimpleTrigger) this.assertJobDetail(TestJob.class);

    assertThat(simpleTrigger.getStartTime())
        .isCloseTo(new DateTime().plusSeconds(40).toDate(), 100l);
    assertThat(simpleTrigger.getJobDataMap().size()).isOne();
    assertThat(simpleTrigger.getJobDataMap().get("A")).isEqualTo(1L);
  }

  @Test
  void testGetTriggerKey() throws SchedulerException {
    final TriggerKey triggerKey = this.osgpScheduler.getTriggerKey(TestJob.class);
    assertThat(triggerKey.getName()).isEqualTo(TestJob.class.getSimpleName() + "-Trigger");
    assertThat(triggerKey.getGroup()).isEqualTo("DEFAULT");
  }

  Trigger assertJobDetail(final Class<? extends Job> jobClazz) throws SchedulerException {

    verify(this.quartzScheduler).addJob(this.jobDetailArgumentCaptor.capture(), eq(true));
    final JobDetail jobDetail = this.jobDetailArgumentCaptor.getValue();

    verify(this.quartzScheduler)
        .scheduleJob(eq(jobDetail), this.jobTriggerSetArgumentCaptor.capture(), eq(true));
    final Set<? extends Trigger> triggers = this.jobTriggerSetArgumentCaptor.getValue();

    assertThat(jobDetail.getKey().getName()).isEqualTo(jobClazz.getSimpleName());
    assertThat(triggers.size()).isOne();

    return triggers.iterator().next();
  }

  void assertCronTrigger(
      final CronTrigger cronTrigger, final String cronExpression, final TimeZone timeZone)
      throws SchedulerException {

    assertThat(cronTrigger.getCronExpression()).isEqualTo(cronExpression);
    assertThat(cronTrigger.getTimeZone()).isEqualTo(timeZone);
  }
}
