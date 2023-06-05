// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.config;

import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties.Builder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OsgpSchedulerConfig extends AbstractOsgpSchedulerConfig {
  private final DataSourceProperties dataSourceProperties;

  public OsgpSchedulerConfig(final DataSourceProperties dataSourceProperties) {
    super("ThrottlingCoreQuartzScheduler");
    this.dataSourceProperties = dataSourceProperties;
  }

  @Override
  protected Builder getSchedulingConfigBuilder() {
    return super.getSchedulingConfigBuilder()
        .withJobStoreDbUrl(this.dataSourceProperties.determineUrl())
        .withJobStoreDbUsername(this.dataSourceProperties.determineUsername())
        .withJobStoreDbPassword(this.dataSourceProperties.determinePassword())
        .withJobStoreDbDriver(this.dataSourceProperties.determineDriverClassName());
  }
}
