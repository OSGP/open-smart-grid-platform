/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.config.scheduling;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.domain.microgrids.application.tasks.CommunicationMonitoringJob;
import org.opensmartgridplatform.shared.application.scheduling.CommunicationMonitoringDisabledCondition;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.shared.application.scheduling.OsgpSchedulingEnabledCondition;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value = { OsgpSchedulingEnabledCondition.class, CommunicationMonitoringDisabledCondition.class })
public class CommunicationMonitoringDisabledConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMonitoringDisabledConfig.class);

    private final OsgpScheduler osgpScheduler;

    @Autowired
    public CommunicationMonitoringDisabledConfig(final OsgpScheduler osgpScheduler) {
        this.osgpScheduler = osgpScheduler;
    }

    @PostConstruct
    private void removeScheduledJob() throws SchedulerException {

        LOGGER.info("Communication monitoring disabled, removing job.");

        this.osgpScheduler.deleteScheduledJob(CommunicationMonitoringJob.class);
    }
}
