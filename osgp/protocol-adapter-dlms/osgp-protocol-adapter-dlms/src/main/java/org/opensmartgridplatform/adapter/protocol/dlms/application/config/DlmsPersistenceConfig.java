// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** An application context Java configuration class. */
@EnableJpaRepositories(
    entityManagerFactoryRef = "dlmsEntityManagerFactory",
    basePackageClasses = {DlmsDeviceRepository.class})
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class DlmsPersistenceConfig extends AbstractPersistenceConfig {

  @Value("${db.username.dlms}")
  private String username;

  @Value("${db.password.dlms}")
  private String password;

  @Value("${db.host.dlms}")
  private String databaseHost;

  @Value("${db.port.dlms}")
  private int databasePort;

  @Value("${db.name.dlms}")
  private String databaseName;

  private HikariDataSource dataSourceDlms;

  public DataSource getDataSourceDlms() {
    if (this.dataSourceDlms == null) {
      final DefaultConnectionPoolFactory.Builder builder =
          super.builder()
              .withUsername(this.username)
              .withPassword(this.password)
              .withDatabaseHost(this.databaseHost)
              .withDatabasePort(this.databasePort)
              .withDatabaseName(this.databaseName);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSourceDlms = factory.getDefaultConnectionPool();
    }
    return this.dataSourceDlms;
  }

  @Override
  @Bean
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Bean(initMethod = "migrate")
  public Flyway dlmsFlyway() {
    return super.createFlyway(this.getDataSourceDlms());
  }

  @Override
  @Bean(name = "dlmsEntityManagerFactory")
  @DependsOn("dlmsFlyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory(
        "OSGP_PROTOCOL_ADAPTER_DLMS_SETTINGS", this.getDataSourceDlms());
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    if (this.dataSourceDlms != null) {
      this.dataSourceDlms.close();
    }
  }
}
