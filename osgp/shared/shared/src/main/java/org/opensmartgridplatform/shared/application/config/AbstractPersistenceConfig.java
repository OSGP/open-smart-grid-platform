/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.ejb.HibernatePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * This class provides the basic components used for persistence.
 */

public abstract class AbstractPersistenceConfig extends AbstractConfig {

    private static final String REGEX_COMMA_WITH_OPTIONAL_WHITESPACE = "\\s*+,\\s*+";

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.driver}")
    private String driverClassName;

    @Value("${db.protocol}")
    private String databaseProtocol;

    @Value("${db.host}")
    private String databaseHost;

    @Value("${db.port:5432}")
    private int databasePort;

    @Value("${db.name}")
    private String databaseName;

    @Value("${db.min_pool_size:1}")
    private int minPoolSize;

    @Value("${db.max_pool_size:5}")
    private int maxPoolSize;

    @Value("${db.auto_commit:false}")
    private boolean isAutoCommit;

    @Value("${db.idle_timeout:120000}")
    private int idleTimeout;

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

    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    @Value("${hibernate.ejb.naming_strategy}")
    private String hibernateNamingStrategy;

    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;

    @Value("${entitymanager.packages.to.scan}")
    private String entitymanagerPackagesToScan;

    private HikariDataSource dataSource;

    protected DataSource getDataSource() {
        if (this.dataSource == null) {
            final DefaultConnectionPoolFactory.Builder builder = this.builder().withUsername(this.username)
                    .withPassword(this.password).withDatabaseHost(this.databaseHost).withDatabasePort(this.databasePort)
                    .withDatabaseName(this.databaseName);
            final DefaultConnectionPoolFactory factory = builder.build();
            this.dataSource = factory.getDefaultConnectionPool();
        }

        return this.dataSource;

    }

    protected JpaTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
        transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ALWAYS);

        return transactionManager;
    }

    protected abstract LocalContainerEntityManagerFactoryBean entityManagerFactory();

    protected Flyway createFlyway() {
        return this.createFlyway(this.getDataSource());
    }

    protected Flyway createFlyway(final DataSource dataSource) {
        final Flyway flyway = new Flyway();

        // Initialization for non-empty schema with no metadata table
        flyway.setBaselineVersion(MigrationVersion.fromVersion(this.flywayInitialVersion));
        flyway.setBaselineDescription(this.flywayInitialDescription);
        flyway.setBaselineOnMigrate(this.flywayInitOnMigrate);
        flyway.setOutOfOrder(true);

        flyway.setDataSource(dataSource);
        return flyway;

    }

    protected LocalContainerEntityManagerFactoryBean entityManagerFactory(final String persistenceUnitName) {
        return this.entityManagerFactory(persistenceUnitName, this.getDataSource(), this.entitymanagerPackagesToScan);
    }

    protected LocalContainerEntityManagerFactoryBean entityManagerFactory(final String persistenceUnitName,
            final DataSource dataSource) {
        return this.entityManagerFactory(persistenceUnitName, dataSource, this.entitymanagerPackagesToScan);
    }

    protected LocalContainerEntityManagerFactoryBean entityManagerFactory(final String persistenceUnitName,
            final DataSource dataSource, final String entitymanagerPackagesToScan) {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
        entityManagerFactoryBean.setDataSource(dataSource);
        if (entitymanagerPackagesToScan.contains(",")) {
            entityManagerFactoryBean
                    .setPackagesToScan(entitymanagerPackagesToScan.split(REGEX_COMMA_WITH_OPTIONAL_WHITESPACE));
        } else {
            entityManagerFactoryBean.setPackagesToScan(entitymanagerPackagesToScan);
        }
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

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
        return new DefaultConnectionPoolFactory.Builder().withDriverClassName(this.driverClassName)
                .withProtocol(this.databaseProtocol).withMinPoolSize(this.minPoolSize).withMaxPoolSize(this.maxPoolSize)
                .withAutoCommit(this.isAutoCommit).withIdleTimeout(this.idleTimeout);
    }
}
