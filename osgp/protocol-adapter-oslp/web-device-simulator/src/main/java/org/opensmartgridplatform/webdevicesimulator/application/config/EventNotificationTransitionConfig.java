package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.EventNotificationTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
@PropertySource("classpath:web-device-simulator.properties")
@PropertySource(value = "file:${osgp/WebDeviceSimulator/config}", ignoreResourceNotFound = true)
public class EventNotificationTransitionConfig implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_EVENTNOTIFICATION_CRON_EXPRESSION =
      "autonomous.tasks.eventnotification.cron.expression";
  private static final String PROPERTY_NAME_AUTONOMOUS_EVENTNOTIFICATION_POOL_SIZE =
      "autonomous.tasks.eventnotification.pool.size";
  private static final String PROPERTY_NAME_AUTONOMOUS_EVENTNOTIFICATION_THREAD_NAME_PREFIX =
      "autonomous.tasks.eventnotification.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private EventNotificationTransition eventNotificationTransition;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.eventNotificationTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(this.eventNotificationTransition, this.eventNotificationTrigger()));
  }

  public CronTrigger eventNotificationTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TASKS_EVENTNOTIFICATION_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler eventNotificationTaskScheduler() {
    final ThreadPoolTaskScheduler eventNotificationTaskScheduler = new ThreadPoolTaskScheduler();
    eventNotificationTaskScheduler.setPoolSize(
        Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_AUTONOMOUS_EVENTNOTIFICATION_POOL_SIZE)));
    eventNotificationTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_EVENTNOTIFICATION_THREAD_NAME_PREFIX));
    eventNotificationTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return eventNotificationTaskScheduler;
  }
}
