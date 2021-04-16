/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.config;

import javax.annotation.Resource;
import org.opensmartgridplatform.webdevicesimulator.application.tasks.TariffSwitchingHigh;
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
public class TariffSwitchingHighConfig implements SchedulingConfigurer {

  private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_HIGH_CRON_EXPRESSION =
      "autonomous.tasks.tariffswitching.high.cron.expression";
  private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_HIGH_POOL_SIZE =
      "autonomous.tasks.tariffswitching.high.pool.size";
  private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_HIGH_THREAD_NAME_PREFIX =
      "autonomous.tasks.tariffswitching.high.thread.name.prefix";

  @Resource private Environment environment;

  @Autowired private TariffSwitchingHigh tariffSwitchingOn;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.tariffSwitchingHighTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(this.tariffSwitchingOn, this.tariffSwitchingOnTrigger()));
  }

  public CronTrigger tariffSwitchingOnTrigger() {
    final String cron =
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_HIGH_CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public TaskScheduler tariffSwitchingHighTaskScheduler() {
    final ThreadPoolTaskScheduler tariffSwitchingHighTaskScheduler = new ThreadPoolTaskScheduler();
    tariffSwitchingHighTaskScheduler.setPoolSize(
        Integer.parseInt(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_HIGH_POOL_SIZE)));
    tariffSwitchingHighTaskScheduler.setThreadNamePrefix(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_HIGH_THREAD_NAME_PREFIX));
    tariffSwitchingHighTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return tariffSwitchingHighTaskScheduler;
  }
}
