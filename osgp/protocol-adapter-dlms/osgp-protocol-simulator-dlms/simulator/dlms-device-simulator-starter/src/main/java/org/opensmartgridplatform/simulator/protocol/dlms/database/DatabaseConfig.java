/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

  @Bean
  @Primary
  @ConfigurationProperties("shared-db.datasource")
  public HikariDataSource sharedDbDatasource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties("core-db.datasource")
  public HikariDataSource coreDbDatasource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties("protocol-adapter-dlms-db.datasource")
  public HikariDataSource protocolAdapterDlmsDbDatasource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name = "sharedDb")
  public JdbcTemplate sharedDb() {
    return new JdbcTemplate(this.sharedDbDatasource());
  }

  @Bean(name = "coreDb")
  public JdbcTemplate coredDb() {
    return new JdbcTemplate(this.coreDbDatasource());
  }

  @Bean(name = "protocolAdapterDlmsDb")
  public JdbcTemplate protocolAdapterDlmsDb() {
    return new JdbcTemplate(this.protocolAdapterDlmsDbDatasource());
  }
}
