/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.shared.application.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DataSourceFactory {

  private final Map<String, DataSource> dataSources = new HashMap<>();

  public Optional<DataSource> getDataSource() {
    return this.getDataSource("default");
  }

  public Optional<DataSource> getDataSource(final String dataSourceName) {
    final DataSource dataSource;
    if (this.dataSources.containsKey(dataSourceName)) {
      dataSource = this.dataSources.get(dataSourceName);
    } else {
      dataSource = this.createDataSource(dataSourceName);
      this.dataSources.put(dataSourceName, dataSource);
    }
    return Optional.ofNullable(dataSource);
  }

  private @Nullable DataSource createDataSource(final String dataSourceName) {
    final DataSource dataSource;
    final String filename = String.format("datasource-%s.properties", dataSourceName);
    final URL url = DataSourceFactory.class.getClassLoader().getResource(filename);
    if (url != null) {
      dataSource = new HikariDataSource(this.getConfiguration(dataSourceName, url));
    } else {
      dataSource = null;
    }
    return dataSource;
  }

  @NotNull
  protected HikariConfig getConfiguration(final String dataSourceName, final URL url) {
    final HikariConfig config = new HikariConfig(url.getFile());
    config.setPoolName(dataSourceName);
    return config;
  }
}
