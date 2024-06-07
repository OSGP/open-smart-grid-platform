// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * This class provides the basic components used for persistence. Configuring the datasource URL can
 * be done in two ways:
 *
 * <ul>
 *   <li><code>db.url</code> with the complete URL. If this property is present it will be used
 *   <li>Separate properties for <code>db.protocol</code>, <code>db.host</code>, <code>db.port
 *       </code> and <code>db.name</code>
 * </ul>
 *
 * You can also combine it when using several databases. Define a global property e.g. <code>
 * db.url=jdbc:postgres://host1:5432,host2:5432/${db.name}</code>. This way you can define <code>
 * db.host</code> per app, but have a global host config.
 */
public abstract class AbstractPersistenceConfig extends AbstractConfig {

  private static final String REGEX_COMMA_WITH_OPTIONAL_WHITESPACE = "\\s*+,\\s*+";

  @Value("${db.username}")
  private String username;

  @Value("${db.password}")
  private String password;

  @Value("${db.driver}")
  private String driverClassName;

  /**
   * The JDBC URL for the database. This can be constructed in two ways, see the class Javadoc for
   * details.
   */
  @SuppressWarnings("squid:S6857") // Sonar doesn't get this
  @Value("${db.url:${db.protocol}${db.host}:${db.port:5432}/${db.name}}")
  private String databaseUrl;

  @Value("${db.min_pool_size:1}")
  private int minPoolSize;

  @Value("${db.max_pool_size:5}")
  private int maxPoolSize;

  @Value("${db.initialization_fail_timeout:300000}")
  private long initializationFailTimeout;

  @Value("${db.validation_timeout:5000}")
  private long validationTimeout;

  @Value("${db.connection_timeout:30000}")
  private long connectionTimeout;

  @Value("${db.auto_commit:false}")
  private boolean isAutoCommit;

  @Value("${db.idle_timeout:120000}")
  private int idleTimeout;

  @Value("${db.max_lifetime:1800000}")
  private int maxLifetime;

  @Value("${flyway.initial.version}")
  private String flywayInitialVersion;

  @Value("${flyway.initial.description}")
  private String flywayInitialDescription;

  @Value("${flyway.init.on.migrate:true}")
  private boolean flywayInitOnMigrate;

  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";

  @Value("${hibernate.dialect}")
  private String hibernateDialect;

  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";

  @Value("${hibernate.format_sql}")
  private String hibernateFormatSql;

  private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
      "hibernate.physical_naming_strategy";

  @Value("${hibernate.physical_naming_strategy}")
  private String hibernateNamingStrategy;

  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

  @Value("${hibernate.show_sql}")
  private String hibernateShowSql;

  @Value("${entitymanager.packages.to.scan}")
  private String entitymanagerPackagesToScan;

  private HikariDataSource dataSource;

  protected DataSource getDataSource() {
    if (this.dataSource == null) {
      final DefaultConnectionPoolFactory factory = this.builder().build();
      this.dataSource = factory.getDefaultConnectionPool();
    }

    return this.dataSource;
  }

  protected JpaTransactionManager transactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
    transactionManager.setTransactionSynchronization(
        AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);

    return transactionManager;
  }

  protected abstract LocalContainerEntityManagerFactoryBean entityManagerFactory();

  protected Flyway createFlyway() {
    return this.createFlyway(this.getDataSource());
  }

  protected Flyway createFlyway(final DataSource dataSource) {
    return Flyway.configure()
        .baselineVersion(MigrationVersion.fromVersion(this.flywayInitialVersion))
        .baselineDescription(this.flywayInitialDescription)
        .baselineOnMigrate(this.flywayInitOnMigrate)
        .outOfOrder(true)
        .table("schema_version")
        .dataSource(dataSource)
        .load();
  }

  protected LocalContainerEntityManagerFactoryBean entityManagerFactory(
      final String persistenceUnitName) {
    return this.entityManagerFactory(
        persistenceUnitName, this.getDataSource(), this.entitymanagerPackagesToScan);
  }

  protected LocalContainerEntityManagerFactoryBean entityManagerFactory(
      final String persistenceUnitName, final DataSource dataSource) {
    return this.entityManagerFactory(
        persistenceUnitName, dataSource, this.entitymanagerPackagesToScan);
  }

  protected LocalContainerEntityManagerFactoryBean entityManagerFactory(
      final String persistenceUnitName,
      final DataSource dataSource,
      final String entitymanagerPackagesToScan) {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
    entityManagerFactoryBean.setDataSource(dataSource);
    if (entitymanagerPackagesToScan.contains(",")) {
      entityManagerFactoryBean.setPackagesToScan(
          entitymanagerPackagesToScan.split(REGEX_COMMA_WITH_OPTIONAL_WHITESPACE));
    } else {
      entityManagerFactoryBean.setPackagesToScan(entitymanagerPackagesToScan);
    }
    entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

    final Properties jpaProperties = new Properties();
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT, this.hibernateDialect);
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, this.hibernateFormatSql);
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, this.hibernateNamingStrategy);
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, this.hibernateShowSql);

    entityManagerFactoryBean.setJpaProperties(jpaProperties);

    return entityManagerFactoryBean;
  }

  protected void destroyDataSource() {
    if (this.dataSource != null) {
      this.dataSource.close();
    }
  }

  protected DefaultConnectionPoolFactory.Builder builder() {
    return new DefaultConnectionPoolFactory.Builder()
        .withDriverClassName(this.driverClassName)
        .withDatabaseUrl(this.databaseUrl)
        .withMinPoolSize(this.minPoolSize)
        .withMaxPoolSize(this.maxPoolSize)
        .withAutoCommit(this.isAutoCommit)
        .withIdleTimeout(this.idleTimeout)
        .withMaxLifetime(this.maxLifetime)
        .withInitializationFailTimeout(this.initializationFailTimeout)
        .withValidationTimeout(this.validationTimeout)
        .withConnectionTimeout(this.connectionTimeout)
        .withUsername(this.username)
        .withPassword(this.password);
  }
}
