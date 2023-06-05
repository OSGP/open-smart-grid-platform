// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.EveningMorningBurnersLightSwitchingOff;
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
public class EveningMorningBurnersLightSwitchingOffConfig implements SchedulingConfigurer {

  private static final String
      PROPERTY_NAME_AUTONOMOUS_TASKS_EVENING_MORNING_BURNER_LIGHTSWITCHING_OFF_CRON_EXPRESSION =
          "autonomous.tasks.evening.morning.burner.lightswitching.off.cron.expression";
  private static final String
      PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_OFF_POOL_SIZE =
          "autonomous.tasks.evening.morning.burner.lightswitching.off.pool.size";
  private static final String
      PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_OFF_THREAD_NAME_PREFIX =
          "autonomous.tasks.evening.morning.burner.lightswitching.off.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private EveningMorningBurnersLightSwitchingOff eveningMorningBurnersLightSwitchingOff;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.eveningMorningBurnerslightSwitchingOffTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(
            this.eveningMorningBurnersLightSwitchingOff,
            this.eveningMorningBurnerslightSwitchingOffTrigger()));
  }

  public CronTrigger eveningMorningBurnerslightSwitchingOffTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TASKS_EVENING_MORNING_BURNER_LIGHTSWITCHING_OFF_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler eveningMorningBurnerslightSwitchingOffTaskScheduler() {
    final ThreadPoolTaskScheduler eveningMorningBurnerslightSwitchingOffTaskScheduler =
        new ThreadPoolTaskScheduler();
    eveningMorningBurnerslightSwitchingOffTaskScheduler.setPoolSize(
        Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_OFF_POOL_SIZE)));
    eveningMorningBurnerslightSwitchingOffTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_EVENING_MORNING_BURNER_LIGHTSWITCHING_OFF_THREAD_NAME_PREFIX));
    eveningMorningBurnerslightSwitchingOffTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return eveningMorningBurnerslightSwitchingOffTaskScheduler;
  }
}
