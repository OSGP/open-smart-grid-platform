// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Persistence configuration for the osgp_adapter_protocol_iec61850 database. */
@EnableJpaRepositories(
    entityManagerFactoryRef = "iec61850EntityManagerFactory",
    basePackageClasses = {Iec61850DeviceRepository.class})
@Configuration
@EnableTransactionManagement()
public class Iec61850PersistenceConfig extends AbstractPersistenceConfig {

  @Value("${db.username.iec61850}")
  private String databaseUsername;

  @Value("${db.password.iec61850}")
  private String databasePassword;

  @Value("${db.host.iec61850}")
  private String databaseHost;

  @Value("${db.port.iec61850}")
  private int databasePort;

  @Value("${db.name.iec61850}")
  private String databaseName;

  private HikariDataSource dataSourceIec61850;

  public DataSource getDataSourceIec61850() {
    if (this.dataSourceIec61850 == null) {
      final DefaultConnectionPoolFactory.Builder builder =
          super.builder()
              .withUsername(this.databaseUsername)
              .withPassword(this.databasePassword)
              .withDatabaseHost(this.databaseHost)
              .withDatabasePort(this.databasePort)
              .withDatabaseName(this.databaseName);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSourceIec61850 = factory.getDefaultConnectionPool();
    }
    return this.dataSourceIec61850;
  }

  @Override
  @Bean
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Bean(initMethod = "migrate")
  public Flyway iec61850Flyway() {
    return super.createFlyway(this.getDataSourceIec61850());
  }

  @Override
  @Bean(name = "iec61850EntityManagerFactory")
  @DependsOn("iec61850Flyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory(
        "OSGP_PROTOCOL_ADAPTER_IEC61850", this.getDataSourceIec61850());
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    if (this.dataSourceIec61850 != null) {
      this.dataSourceIec61850.close();
    }
  }
}
