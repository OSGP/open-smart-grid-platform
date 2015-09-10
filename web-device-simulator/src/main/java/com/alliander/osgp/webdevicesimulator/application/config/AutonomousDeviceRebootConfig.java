/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.application.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.webdevicesimulator.application.tasks.AutonomousDeviceReboot;

@Configuration
@EnableScheduling
@PropertySource("file:${osp//webDeviceSimulator/config}")
public class AutonomousDeviceRebootConfig {

    private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_DEVICE_REBOOT_CRON_EXPRESSION = "autonomous.tasks.device.reboot.cron.expression";
    private static final String PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_POOL_SIZE = "autonomous.task.device.reboot.pool.size";
    private static final String PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_THREAD_NAME_PREFIX = "autonomous.task.device.reboot.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private AutonomousDeviceReboot autonomousDeviceReboot;

    @Bean
    public CronTrigger autonomousDeviceRebootTrigger() {
        final String cron = this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_DEVICE_REBOOT_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler deviceRebootTaskScheduler() {
        final ThreadPoolTaskScheduler deviceRebootTaskScheduler = new ThreadPoolTaskScheduler();
        deviceRebootTaskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_POOL_SIZE)));
        deviceRebootTaskScheduler.setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_DEVICE_REBOOT_THREAD_NAME_PREFIX));
        deviceRebootTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        deviceRebootTaskScheduler.setAwaitTerminationSeconds(10);
        deviceRebootTaskScheduler.initialize();
        deviceRebootTaskScheduler.schedule(this.autonomousDeviceReboot, this.autonomousDeviceRebootTrigger());
        return deviceRebootTaskScheduler;
    }

}
