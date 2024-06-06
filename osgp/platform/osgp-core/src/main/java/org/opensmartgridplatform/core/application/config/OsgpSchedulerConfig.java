// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OsgpSchedulerConfig extends AbstractOsgpSchedulerConfig {

  public OsgpSchedulerConfig() {
    super("CoreQuartzScheduler");
  }
}
