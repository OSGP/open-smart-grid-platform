/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.config.scheduling;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.domain.da.application.tasks.CommunicationMonitoringJob;
import org.opensmartgridplatform.shared.application.scheduling.CommunicationMonitoringEnabledCondition;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.shared.application.scheduling.OsgpSchedulingEnabledCondition;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value = { OsgpSchedulingEnabledCondition.class, CommunicationMonitoringEnabledCondition.class })
public class CommunicationMonitoringEnabledConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMonitoringEnabledConfig.class);

    private final String cronExpressionCommunicationMonitoring;

    private final OsgpScheduler osgpScheduler;

    @Autowired
    public CommunicationMonitoringEnabledConfig(final OsgpScheduler osgpScheduler,
            @Value("${communication.monitoring.cron.expression}") final String cronExpressionCommunicationMonitoring) {
        this.osgpScheduler = osgpScheduler;
        this.cronExpressionCommunicationMonitoring = cronExpressionCommunicationMonitoring;
    }

    @PostConstruct
    private void initializeScheduledJob() throws SchedulerException {

        LOGGER.info("Communication monitoring enabled, scheduling job.");

        this.osgpScheduler.createAndScheduleJob(CommunicationMonitoringJob.class,
                this.cronExpressionCommunicationMonitoring);
    }
}
