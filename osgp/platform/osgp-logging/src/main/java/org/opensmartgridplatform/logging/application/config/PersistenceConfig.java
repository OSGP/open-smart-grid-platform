/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.application.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.logging.domain.repositories.WebServiceMonitorLogRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@Configuration
@EnableJpaRepositories(basePackageClasses = {WebServiceMonitorLogRepository.class})
@EnableTransactionManagement
public class PersistenceConfig extends AbstractConfig {

  private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
  private static final String PROPERTY_NAME_DATABASE_PW = "db.password";

  private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
  private static final String PROPERTY_NAME_DATABASE_PROTOCOL = "db.protocol";

  private static final String PROPERTY_NAME_DATABASE_HOST = "db.host";
  private static final String PROPERTY_NAME_DATABASE_PORT = "db.port";
  private static final String PROPERTY_NAME_DATABASE_NAME = "db.name";

  private static final String PROPERTY_NAME_DATABASE_MIN_POOL_SIZE = "db.min_pool_size";
  private static final String PROPERTY_NAME_DATABASE_MAX_POOL_SIZE = "db.max_pool_size";
  private static final String PROPERTY_NAME_DATABASE_AUTO_COMMIT = "db.auto_commit";
  private static final String PROPERTY_NAME_DATABASE_IDLE_TIMEOUT = "db.idle_timeout";

  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
      "hibernate.physical_naming_strategy";
  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

  private static final String PROPERTY_NAME_FLYWAY_INITIAL_VERSION = "flyway.initial.version";
  private static final String PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION =
      "flyway.initial.description";
  private static final String PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE = "flyway.init.on.migrate";

  private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN =
      "entitymanager.packages.to.scan";

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);

  private HikariDataSource dataSource;

  /** Method for creating the Data Source. */
  public DataSource getDataSource() {
    if (this.dataSource == null) {
      final String username = this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME);
      final String password = this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PW);

      final String driverClassName =
          this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER);
      final String databaseProtocol =
          this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PROTOCOL);

      final String databaseHost = this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_HOST);
      final int databasePort =
          Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PORT));
      final String databaseName = this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_NAME);

      final int minPoolSize =
          Integer.parseInt(
              this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_MIN_POOL_SIZE));
      final int maxPoolSize =
          Integer.parseInt(
              this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE));
      final boolean isAutoCommit =
          Boolean.parseBoolean(
              this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT));
      final int idleTimeout =
          Integer.parseInt(
              this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_IDLE_TIMEOUT));

      final DefaultConnectionPoolFactory.Builder builder =
          new DefaultConnectionPoolFactory.Builder()
              .withUsername(username)
              .withPassword(password)
              .withDriverClassName(driverClassName)
              .withProtocol(databaseProtocol)
              .withDatabaseHost(databaseHost)
              .withDatabasePort(databasePort)
              .withDatabaseName(databaseName)
              .withMinPoolSize(minPoolSize)
              .withMaxPoolSize(maxPoolSize)
              .withAutoCommit(isAutoCommit)
              .withIdleTimeout(idleTimeout);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSource = factory.getDefaultConnectionPool();
    }

    return this.dataSource;
  }

  /** Method for creating the Transaction Manager. */
  @Bean
  public JpaTransactionManager transactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();

    try {
      transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
      transactionManager.setTransactionSynchronization(
          AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);
    } catch (final Exception e) {
      final String msg = "Error in creating transaction manager bean";
      LOGGER.error(msg, e);
      throw e;
    }

    return transactionManager;
  }

  @Bean(initMethod = "migrate")
  public Flyway loggingFlyway() {
    return Flyway.configure()
        .baselineVersion(
            MigrationVersion.fromVersion(
                this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_VERSION)))
        .baselineDescription(
            this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION))
        .baselineOnMigrate(
            Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE)))
        .outOfOrder(true)
        .table("schema_version")
        .dataSource(this.getDataSource())
        .load();
  }

  /** Method for creating the Entity Manager Factory Bean. */
  @Bean
  @DependsOn("loggingFlyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactoryBean.setPersistenceUnitName("OSGP_LOGGING");
    entityManagerFactoryBean.setDataSource(this.getDataSource());
    entityManagerFactoryBean.setPackagesToScan(
        this.environment.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
    entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

    final Properties jpaProperties = new Properties();
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_DIALECT,
        this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
        this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
        this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_SHOW_SQL,
        this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

    entityManagerFactoryBean.setJpaProperties(jpaProperties);

    return entityManagerFactoryBean;
  }

  @PreDestroy
  public void destroyDataSource() {
    if (this.dataSource != null) {
      this.dataSource.close();
    }
  }
}
