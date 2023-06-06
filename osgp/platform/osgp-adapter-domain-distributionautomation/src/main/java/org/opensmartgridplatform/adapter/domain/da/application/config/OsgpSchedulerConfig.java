// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.config;

import org.opensmartgridplatform.adapter.domain.da.application.config.scheduling.CommunicationMonitoringDisabledConfig;
import org.opensmartgridplatform.adapter.domain.da.application.config.scheduling.CommunicationMonitoringEnabledConfig;
import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.opensmartgridplatform.shared.application.scheduling.OsgpSchedulingEnabledCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Conditional(value = {OsgpSchedulingEnabledCondition.class})
@Configuration
@Import({CommunicationMonitoringEnabledConfig.class, CommunicationMonitoringDisabledConfig.class})
public class OsgpSchedulerConfig extends AbstractOsgpSchedulerConfig {

  public OsgpSchedulerConfig() {
    super("DomainDistributionAutomationQuartzScheduler");
  }
}
