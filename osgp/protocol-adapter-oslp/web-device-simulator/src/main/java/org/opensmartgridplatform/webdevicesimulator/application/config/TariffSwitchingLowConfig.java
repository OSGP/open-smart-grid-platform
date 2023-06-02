//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.TariffSwitchingLow;
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
public class TariffSwitchingLowConfig implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_LOW_CRON_EXPRESSION =
      "autonomous.tasks.tariffswitching.low.cron.expression";
  private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_POOL_SIZE =
      "autonomous.tasks.tariffswitching.low.pool.size";
  private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_THREAD_NAME_PREFIX =
      "autonomous.tasks.tariffswitching.low.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private TariffSwitchingLow tariffSwitchingLow;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.tariffSwitchingLowTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(this.tariffSwitchingLow, this.tariffSwitchingLowTrigger()));
  }

  public CronTrigger tariffSwitchingLowTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_LOW_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler tariffSwitchingLowTaskScheduler() {
    final ThreadPoolTaskScheduler tariffSwitchingLowTaskScheduler = new ThreadPoolTaskScheduler();
    tariffSwitchingLowTaskScheduler.setPoolSize(
        Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_POOL_SIZE)));
    tariffSwitchingLowTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_THREAD_NAME_PREFIX));
    tariffSwitchingLowTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return tariffSwitchingLowTaskScheduler;
  }
}
