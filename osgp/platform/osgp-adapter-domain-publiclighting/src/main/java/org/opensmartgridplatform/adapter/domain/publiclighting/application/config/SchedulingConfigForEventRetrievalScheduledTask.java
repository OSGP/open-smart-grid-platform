//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import java.util.TimeZone;
import java.util.concurrent.Executor;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks.EventRetrievalScheduledTask;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@EnableScheduling
@Configuration
@PropertySource("classpath:osgp-adapter-domain-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true)
public class SchedulingConfigForEventRetrievalScheduledTask extends AbstractConfig
    implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_CRON_EXPRESSION =
      "scheduling.task.event.retrieval.cron.expression";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_CRON_TIMEZONE =
      "scheduling.task.event.retrieval.cron.timezone";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_POOL_SIZE =
      "scheduling.task.event.retrieval.pool.size";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_MANUFACTURER_NAME =
      "scheduling.task.event.retrieval.manufacturer.name";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_MAX_ALLOWED_AGE =
      "scheduling.task.event.retrieval.max.allowed.age";

  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_BACK_OFF_MULTIPLIER =
      "scheduling.task.event.retrieval.back.off.multiplier";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_DEFAULT_WAIT_TIME =
      "scheduling.task.event.retrieval.default.wait.time";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_MAX_BACKOFF =
      "scheduling.task.event.retrieval.max.backoff";
  private static final String PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_HYSTERESIS =
      "scheduling.task.event.retrieval.hysteresis";

  @Autowired private EventRetrievalScheduledTask eventRetrievalScheduledTask;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.eventRetrievalScheduler());
    taskRegistrar.addCronTask(
        new CronTask(
            this.eventRetrievalScheduledTask, this.eventRetrievalScheduledTaskCronTrigger()));
  }

  public CronTrigger eventRetrievalScheduledTaskCronTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_CRON_EXPRESSION);
    final String timezone =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_CRON_TIMEZONE);

    return new CronTrigger(cron, TimeZone.getTimeZone(timezone));
  }

  @Bean(destroyMethod = "shutdown")
  public Executor eventRetrievalScheduler() {
    final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(
        this.getNonRequiredIntegerPropertyValue(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_POOL_SIZE, 10));
    taskScheduler.setThreadNamePrefix(
        "osgp-adapter-domain-publiclighting-event-retrieval-scheduled-task-");
    taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return taskScheduler;
  }

  @Bean
  public String eventRetrievalScheduledTaskManufacturerName() {
    return this.environment.getRequiredProperty(
        PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_MANUFACTURER_NAME);
  }

  @Bean
  public int eventRetrievalScheduledTaskMaximumAllowedAge() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_MAX_ALLOWED_AGE));
  }

  @Bean
  public int eventRetrievalScheduledTaskBackOffMultiplier() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_BACK_OFF_MULTIPLIER));
  }

  @Bean
  public int eventRetrievalScheduledTaskDefaultWaitTime() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_DEFAULT_WAIT_TIME));
  }

  @Bean
  public int eventRetrievalScheduledTaskMaxBackoff() {
    return Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_MAX_BACKOFF))
        * 60;
  }

  @Bean
  public int eventRetrievalScheduledTaskHysteresis() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SCHEDULING_TASK_EVENT_RETRIEVAL_HYSTERESIS));
  }
}
