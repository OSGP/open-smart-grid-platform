// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.domain.config;

import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.opensmartgridplatform.shared.application.config.AbstractCustomConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@EnableJpaRepositories(
    entityManagerFactoryRef = "readableEntityManagerFactory",
    basePackageClasses = {DeviceLogItemPagingRepository.class})
@Configuration
@EnableTransactionManagement()
public class ReadOnlyLoggingConfig extends AbstractCustomConfig {

  private static final String PROPERTY_NAME_DATABASE_USERNAME =
      "db.readonly.username.domain_logging";
  private static final String PROPERTY_NAME_DATABASE_PW = "db.readonly.password.domain_logging";

  private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
  private static final String PROPERTY_NAME_DATABASE_PROTOCOL = "db.protocol";

  private static final String PROPERTY_NAME_DATABASE_HOST = "db.host.domain_logging";
  private static final String PROPERTY_NAME_DATABASE_PORT = "db.port.domain_logging";
  private static final String PROPERTY_NAME_DATABASE_NAME = "db.name.domain_logging";

  private static final String PROPERTY_NAME_DATABASE_MIN_POOL_SIZE = "db.readonly.min_pool_size";
  private static final String PROPERTY_NAME_DATABASE_MAX_POOL_SIZE = "db.readonly.max_pool_size";
  private static final String PROPERTY_NAME_DATABASE_AUTO_COMMIT = "db.readonly.auto_commit";
  private static final String PROPERTY_NAME_DATABASE_IDLE_TIMEOUT = "db.readonly.idle_timeout";

  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
      "hibernate.physical_naming_strategy";
  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

  private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN =
      "entitymanager.packages.to.scan.domain_logging";

  private static final Logger LOGGER = LoggerFactory.getLogger(ReadOnlyLoggingConfig.class);

  private HikariDataSource dataSource;

  /**
   * Wire property sources to local environment.
   *
   * @throws IOException when required property source is not found.
   */
  @PostConstruct
  protected void init() throws IOException {
    this.addPropertySource("file:${osgp/DomainLogging/config}", true);
    this.addPropertySource("file:${osgp/Global/config}", true);
    this.addPropertySource("classpath:osgp-domain-logging.properties", false);
  }

  /**
   * Method for creating the Data Source.
   *
   * @return DataSource
   */
  public DataSource getReadableDataSource() {
    if (this.dataSource == null) {
      final String username = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME);
      final String password = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PW);

      final String driverClassName = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER);
      final String databaseProtocol =
          ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PROTOCOL);

      final String databaseHost = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_HOST);
      final int databasePort =
          Integer.parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PORT));
      final String databaseName = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_NAME);

      final int minPoolSize =
          Integer.parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_MIN_POOL_SIZE));
      final int maxPoolSize =
          Integer.parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE));
      final boolean isAutoCommit =
          Boolean.parseBoolean(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT));
      final int idleTimeout =
          Integer.parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_IDLE_TIMEOUT));

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

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean
  public JpaTransactionManager readableTransactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();

    try {
      transactionManager.setEntityManagerFactory(this.readableEntityManagerFactory().getObject());
      transactionManager.setTransactionSynchronization(
          AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);
    } catch (final Exception e) {
      final String msg = "Error in creating transaction manager bean";
      LOGGER.error(msg, e);
      throw e;
    }

    return transactionManager;
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean readableEntityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactoryBean.setPersistenceUnitName("OSGP_DOMAIN_LOGGING");
    entityManagerFactoryBean.setDataSource(this.getReadableDataSource());
    entityManagerFactoryBean.setPackagesToScan(
        ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
    entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

    final Properties jpaProperties = new Properties();
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_DIALECT,
        ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
        ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
        ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
    jpaProperties.put(
        PROPERTY_NAME_HIBERNATE_SHOW_SQL,
        ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

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
