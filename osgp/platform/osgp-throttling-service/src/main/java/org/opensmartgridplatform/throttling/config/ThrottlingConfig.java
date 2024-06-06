// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.config;

import com.zaxxer.hikari.util.DriverDataSource;
import java.util.Properties;
import org.opensmartgridplatform.throttling.service.PermitReleasedNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrottlingConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingConfig.class);

  @Value("${spring.datasource.url}")
  private String jdbcUrl;

  @Value("${spring.datasource.driver-class-name}")
  private String databaseDriver;

  @Value("${spring.datasource.username}")
  private String databaseUsername;

  @Value("${spring.datasource.password}")
  private String databasePassword;

  @Bean
  public PermitReleasedNotifier permitReleasedNotifier() {
    LOGGER.info("Created jdbcUrl {} for permitReleasedNotifier", this.jdbcUrl);
    final DriverDataSource dataSource =
        new DriverDataSource(
            this.jdbcUrl,
            this.databaseDriver,
            new Properties(),
            this.databaseUsername,
            this.databasePassword);

    return new PermitReleasedNotifier(dataSource);
  }
}
