// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.application.config;

import jakarta.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.LightSwitchingOn;
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
public class LightSwitchingOnConfig implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_LIGHTSWITCHING_ON_CRON_EXPRESSION =
      "autonomous.tasks.lightswitching.on.cron.expression";
  private static final String PROPERTY_NAME_AUTONOMOUS_LIGHTSWITCHING_ON_POOL_SIZE =
      "autonomous.tasks.lightswitching.on.pool.size";
  private static final String PROPERTY_NAME_AUTONOMOUS_LIGHTSWITCHING_ON_THREAD_NAME_PREFIX =
      "autonomous.tasks.lightswitching.on.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private LightSwitchingOn lightSwitchingOn;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.lightSwitchingOnTaskScheduler());
    taskRegistrar.addCronTask(new CronTask(this.lightSwitchingOn, this.lightSwitchingOnTrigger()));
  }

  public CronTrigger lightSwitchingOnTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TASKS_LIGHTSWITCHING_ON_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler lightSwitchingOnTaskScheduler() {
    final ThreadPoolTaskScheduler lightSwitchingOnTaskScheduler = new ThreadPoolTaskScheduler();
    lightSwitchingOnTaskScheduler.setPoolSize(
        Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_AUTONOMOUS_LIGHTSWITCHING_ON_POOL_SIZE)));
    lightSwitchingOnTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_LIGHTSWITCHING_ON_THREAD_NAME_PREFIX));
    lightSwitchingOnTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return lightSwitchingOnTaskScheduler;
  }
}
