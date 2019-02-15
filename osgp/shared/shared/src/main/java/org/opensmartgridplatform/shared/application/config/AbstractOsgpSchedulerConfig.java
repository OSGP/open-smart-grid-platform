/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Bean;

/**
 * This class provides the basic {@link Bean} function to create an
 * {@link OsgpScheduler} instance, which uses a Quartz {@link Scheduler}.
 */
public class AbstractOsgpSchedulerConfig extends AbstractSchedulingConfig {

    private final String schedulerName;

    /**
     * Constructor used to specify the scheduler name, which will be set as
     * Quartz {@link Scheduler} property
     * {@code org.quartz.scheduler.instanceName}.
     *
     * @param schedulerName
     *            The scheduler name.
     */
    public AbstractOsgpSchedulerConfig(final String schedulerName) {
        this.schedulerName = schedulerName;
    }

    /**
     * Creates an instance of {@link OsgpScheduler} and starts the Quartz
     * {@link Scheduler}.
     *
     * @return An instance of {@link OsgpScheduler},
     */
    @Bean
    public OsgpScheduler osgpScheduler() throws SchedulerException {
        final SchedulingConfigProperties schedulingConfigProperties = SchedulingConfigProperties.newBuilder()
                .withSchedulerName(this.schedulerName).withThreadCountKey(KEY_QUARTZ_SCHEDULER_THREAD_COUNT)
                .withJobStoreDbUrl(this.getDatabaseUrl()).withJobStoreDbUsername(this.databaseUsername)
                .withJobStoreDbPassword(this.databasePassword).withJobStoreDbDriver(this.databaseDriver).build();

        final Scheduler quartzScheduler = this.constructAndStartQuartzScheduler(schedulingConfigProperties);
        return new OsgpScheduler(quartzScheduler);
    }
}
