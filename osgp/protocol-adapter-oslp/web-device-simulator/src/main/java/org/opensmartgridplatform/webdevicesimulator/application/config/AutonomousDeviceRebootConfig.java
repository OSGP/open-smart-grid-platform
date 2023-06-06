// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.AutonomousDeviceReboot;
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
public class AutonomousDeviceRebootConfig implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_DEVICE_REBOOT_CRON_EXPRESSION =
      "autonomous.tasks.device.reboot.cron.expression";
  private static final String PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_POOL_SIZE =
      "autonomous.task.device.reboot.pool.size";
  private static final String PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_THREAD_NAME_PREFIX =
      "autonomous.task.device.reboot.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private AutonomousDeviceReboot autonomousDeviceReboot;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.deviceRebootTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(this.autonomousDeviceReboot, this.autonomousDeviceRebootTrigger()));
  }

  public CronTrigger autonomousDeviceRebootTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TASKS_DEVICE_REBOOT_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler deviceRebootTaskScheduler() {
    final ThreadPoolTaskScheduler deviceRebootTaskScheduler = new ThreadPoolTaskScheduler();
    deviceRebootTaskScheduler.setPoolSize(
        Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_POOL_SIZE)));
    deviceRebootTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_THREAD_NAME_PREFIX));
    deviceRebootTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return deviceRebootTaskScheduler;
  }
}
