//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemSlicingRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@EnableJpaRepositories(
    entityManagerFactoryRef = "domainLoggingEntityManagerFactory",
    basePackageClasses = {DeviceLogItemSlicingRepository.class})
@Configuration
@PropertySource("classpath:osgp-adapter-domain-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainCore/config}", ignoreResourceNotFound = true)
public class PersistenceDomainLoggingConfig extends AbstractPersistenceConfig {

  @Value("${db.username.domain_logging}")
  private String username;

  @Value("${db.password.domain_logging}")
  private String password;

  @Value("${db.host.domain_logging}")
  private String databaseHost;

  @Value("${db.port.domain_logging}")
  private int databasePort;

  @Value("${db.name.domain_logging}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.domain_logging}")
  private String packagesToScan;

  private HikariDataSource dataSource;

  @Override
  public DataSource getDataSource() {
    if (this.dataSource == null) {
      final DefaultConnectionPoolFactory.Builder builder =
          this.builder()
              .withUsername(this.username)
              .withPassword(this.password)
              .withDatabaseHost(this.databaseHost)
              .withDatabasePort(this.databasePort)
              .withDatabaseName(this.databaseName);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSource = factory.getDefaultConnectionPool();
    }

    return this.dataSource;
  }

  @Bean
  public JpaTransactionManager domainLoggingTransactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
    transactionManager.setTransactionSynchronization(
        AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);

    return transactionManager;
  }

  @Override
  @Bean("domainLoggingEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        super.entityManagerFactory("OSGP_DOMAIN_LOGGING");
    entityManagerFactoryBean.setDataSource(this.getDataSource());
    entityManagerFactoryBean.setPackagesToScan(this.packagesToScan);

    return entityManagerFactoryBean;
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    super.destroyDataSource();
  }
}
