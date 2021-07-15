/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.config;

import org.opensmartgridplatform.adapter.domain.microgrids.application.config.scheduling.CommunicationMonitoringDisabledConfig;
import org.opensmartgridplatform.adapter.domain.microgrids.application.config.scheduling.CommunicationMonitoringEnabledConfig;
import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.opensmartgridplatform.shared.application.scheduling.OsgpSchedulingEnabledCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Conditional(value = { OsgpSchedulingEnabledCondition.class })
@Configuration
@Import({ CommunicationMonitoringEnabledConfig.class, CommunicationMonitoringDisabledConfig.class })
public class OsgpSchedulerConfig extends AbstractOsgpSchedulerConfig {

    public OsgpSchedulerConfig() {
        super("DomainMicrogridsQuartzScheduler");
    }

}
