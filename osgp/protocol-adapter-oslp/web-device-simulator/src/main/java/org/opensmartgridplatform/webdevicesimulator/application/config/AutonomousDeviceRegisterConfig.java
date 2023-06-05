// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.AutonomousDeviceRegister;
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
public class AutonomousDeviceRegisterConfig implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_CRON_EXPRESSION =
      "autonomous.tasks.device.registration.cron.expression";
  private static final String PROPERTY_NAME_AUTONOMOUS_POOL_SIZE =
      "autonomous.task.device.registration.pool.size";
  private static final String PROPERTY_NAME_AUTONOMOUS_THREAD_NAME_PREFIX =
      "autonomous.task.device.registration.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private AutonomousDeviceRegister autonomousDeviceRegister;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.deviceRegistrationTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(this.autonomousDeviceRegister, this.autonomousDeviceRegisterTrigger()));
  }

  public CronTrigger autonomousDeviceRegisterTrigger() {
    final String cron =
        this.environment.getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler deviceRegistrationTaskScheduler() {
    final ThreadPoolTaskScheduler deviceRegistrationTaskScheduler = new ThreadPoolTaskScheduler();
    deviceRegistrationTaskScheduler.setPoolSize(
        Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_POOL_SIZE)));
    deviceRegistrationTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_THREAD_NAME_PREFIX));
    deviceRegistrationTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return deviceRegistrationTaskScheduler;
  }
}
