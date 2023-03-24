/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.health;

import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.opensmartgridplatform.shared.config.AppHealthEnabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(AppHealthEnabledCondition.class)
public class DataSourceHealthCheck implements HealthCheck {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceHealthCheck.class);

  private final DataSource dataSource;

  public DataSourceHealthCheck(final Optional<DataSource> optionalDataSource) {
    if (optionalDataSource.isPresent()) {
      this.dataSource = optionalDataSource.get();
    } else {
      this.dataSource = null;
      LOGGER.info("No datasource found for HealtCheck");
    }
  }

  @Override
  public HealthResponse isHealthy() {
    try {
      if (this.dataSource != null) {
        return this.checkConnection();
      } else {
        return HealthResponse.ok();
      }
    } catch (final SQLException e) {
      return HealthResponse.notOk(e.getMessage());
    }
  }

  @NotNull
  private HealthResponse checkConnection() throws SQLException {
    try (final var connection = this.dataSource.getConnection()) {
      if (connection.isValid(2)) {
        return HealthResponse.ok();
      } else {
        return HealthResponse.notOk("Connection is present but not valid");
      }
    }
  }
}
