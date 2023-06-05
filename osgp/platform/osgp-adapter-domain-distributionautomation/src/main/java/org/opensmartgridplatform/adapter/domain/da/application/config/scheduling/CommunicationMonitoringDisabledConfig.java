// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.config.scheduling;

import org.opensmartgridplatform.adapter.domain.da.application.tasks.CommunicationMonitoringJob;
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
