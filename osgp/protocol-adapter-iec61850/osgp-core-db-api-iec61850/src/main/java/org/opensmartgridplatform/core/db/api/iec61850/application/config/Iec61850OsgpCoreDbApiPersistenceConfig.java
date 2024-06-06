// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.db.api.iec61850.application.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import org.opensmartgridplatform.core.db.api.iec61850.repositories.SsldDataRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(
    entityManagerFactoryRef = "iec61850OsgpCoreDbApiEntityManagerFactory",
    basePackageClasses = {SsldDataRepository.class})
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-core-db-api-iec61850.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/CoreDbApiIec61850/config}", ignoreResourceNotFound = true)
public class Iec61850OsgpCoreDbApiPersistenceConfig extends AbstractPersistenceConfig {

  @Value("${db.api.username.iec61850}")
  private String username;

  @Value("${db.api.password.iec61850}")
  private String password;

  @Value("${db.api.host.iec61850}")
  private String databaseHost;

  @Value("${db.api.port.iec61850}")
  private int databasePort;

  @Value("${db.api.name.iec61850}")
  private String databaseName;

  @Value("${api.entitymanager.packages.to.scan.iec61850}")
  private String entitymanagerPackagesToScan;

  private HikariDataSource dataSourceCore;

  private DataSource getDataSourceCore() {

    if (this.dataSourceCore == null) {

      final DefaultConnectionPoolFactory.Builder builder =
          super.builder()
              .withUsername(this.username)
              .withPassword(this.password)
              .withDatabaseHost(this.databaseHost)
              .withDatabasePort(this.databasePort)
              .withDatabaseName(this.databaseName);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSourceCore = factory.getDefaultConnectionPool();
    }

    return this.dataSourceCore;
  }

  @Override
  @Bean(name = "iec61850OsgpCoreDbApiTransactionManager")
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Override
  @Bean(name = "iec61850OsgpCoreDbApiEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory(
        "OSGP_CORE_DB_API_IEC61850", this.getDataSourceCore(), this.entitymanagerPackagesToScan);
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    if (this.dataSourceCore != null) {
      this.dataSourceCore.close();
    }
  }
}
