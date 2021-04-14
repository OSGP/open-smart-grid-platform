/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.config.scheduling;

import org.opensmartgridplatform.adapter.domain.microgrids.application.tasks.CommunicationMonitoringJob;
import org.opensmartgridplatform.shared.application.config.scheduling.JobDisabledConfig;
import org.opensmartgridplatform.shared.application.scheduling.CommunicationMonitoringDisabledCondition;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.opensmartgridplatform.shared.application.scheduling.OsgpSchedulingEnabledCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(
    value = {OsgpSchedulingEnabledCondition.class, CommunicationMonitoringDisabledCondition.class})
public class CommunicationMonitoringDisabledConfig
    extends JobDisabledConfig<CommunicationMonitoringJob> {

  @Autowired
  public CommunicationMonitoringDisabledConfig(final OsgpScheduler osgpScheduler) {
    super(CommunicationMonitoringJob.class, osgpScheduler);
  }
}
