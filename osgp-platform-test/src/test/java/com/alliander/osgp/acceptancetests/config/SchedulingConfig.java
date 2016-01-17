/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.core.application.tasks.ScheduledTaskScheduler;

@EnableScheduling
public class SchedulingConfig {

    private static final String SCHEDULING_SCHEDULED_TASKS_CRON_EXPRESSION = "0 */5 * * * *";
    private static final int SCHEDULING_TASK_SCHEDULER_POOL_SIZE = 1;
    private static final String SCHEDULING_TASK_SCHEDULER_THREAD_NAME_PREFIX = "osgp-test-scheduling-";

    @Autowired
    private ScheduledTaskScheduler scheduledTaskScheduler;

    @Bean
    public CronTrigger scheduledTasksCronTrigger() {
        return new CronTrigger(SCHEDULING_SCHEDULED_TASKS_CRON_EXPRESSION);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(SCHEDULING_TASK_SCHEDULER_POOL_SIZE);
        taskScheduler.setThreadNamePrefix(SCHEDULING_TASK_SCHEDULER_THREAD_NAME_PREFIX);
        taskScheduler.initialize();
        taskScheduler.schedule(this.scheduledTaskScheduler, this.scheduledTasksCronTrigger());
        return taskScheduler;
    }
}
