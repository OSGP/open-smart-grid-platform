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

import org.opensmartgridplatform.adapter.domain.da.application.tasks.CommunicationMonitoringJob;
import org.opensmartgridplatform.shared.application.config.scheduling.JobEnabledConfig;
import org.opensmartgridplatform.shared.application.scheduling.CommunicationMonitoringEnabledCondition;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.shared.application.scheduling.OsgpSchedulingEnabledCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value = { OsgpSchedulingEnabledCondition.class, CommunicationMonitoringEnabledCondition.class })
public class CommunicationMonitoringEnabledConfig extends JobEnabledConfig<CommunicationMonitoringJob> {

    @Autowired
    public CommunicationMonitoringEnabledConfig(final OsgpScheduler osgpScheduler,
            @Value("${communication.monitoring.cron.expression:0 */5 * * * ?}") final String jobCronExpression) {
        super(CommunicationMonitoringJob.class, osgpScheduler, jobCronExpression);
    }
}
