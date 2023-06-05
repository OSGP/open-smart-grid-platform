// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import java.util.concurrent.Executor;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks.DeviceConnection104LmdScheduledTask;
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
public class SchedulingConfigForDeviceConnection104LmdScheduledTask extends AbstractConfig
    implements SchedulingConfigurer {

  private static final String CRON_EXPRESSION =
      "scheduling.task.device.connection.104.lmd.cron.expression";
  private static final String POOL_SIZE = "scheduling.task.device.connection.104.lmd.pool.size";
  private static final String MAX_ALLOWED_AGE =
      "scheduling.task.device.connection.104.lmd.max.allowed.age";

  @Autowired private DeviceConnection104LmdScheduledTask deviceConnection104LmdScheduledTask;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.deviceConnection104LmdTaskScheduler());
    taskRegistrar.addCronTask(
        new CronTask(
            this.deviceConnection104LmdScheduledTask,
            this.deviceConnection104LmdScheduledTaskCronTrigger()));
  }

  public CronTrigger deviceConnection104LmdScheduledTaskCronTrigger() {
    final String cron = this.environment.getRequiredProperty(CRON_EXPRESSION);
    return new CronTrigger(cron);
  }

  @Bean(destroyMethod = "shutdown")
  public Executor deviceConnection104LmdTaskScheduler() {
    final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(this.getNonRequiredIntegerPropertyValue(POOL_SIZE, 10));
    taskScheduler.setThreadNamePrefix(
        "osgp-adapter-domain-publiclighting-device-connection-104-lmd-scheduled-task-");
    taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
    return taskScheduler;
  }

  @Bean
  public int deviceConnection104LmdScheduledTaskMaximumAllowedAge() {
    return Integer.parseInt(this.environment.getRequiredProperty(MAX_ALLOWED_AGE));
  }
}
