/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.cucumber.core.config.BaseApplicationConfiguration;
import com.alliander.osgp.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Base class for the application persistence configuration.
 */
@Configuration
@EnableTransactionManagement
public abstract class ApplicationPersistenceConfiguration extends BaseApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPersistenceConfiguration.class);

    private static final String REGEX_COMMA_WITH_OPTIONAL_WHITESPACE = "\\s*+,\\s*+";

    @Value("${db.driver}")
    protected String databaseDriver;

    @Value("${db.protocol}")
    protected String databaseProtocol;

    @Value("${db.username}")
    protected String databaseUsername;

    @Value("${db.password}")
    protected String databasePassword;

    @Value("${db.hostname}")
    protected String databaseHostname;

    @Value("${db.port}")
    protected int databasePort;

    @Value("${db.min_pool_size}")
    protected int databaseMinPoolSize;
    @Value("${db.max_pool_size}")
    protected int databaseMaxPoolSize;
    @Value("${db.auto_commit}")
    protected boolean databaseAutoCommit;
    @Value("${db.idle_timeout}")
    protected int databaseIdleTimeout;

    protected static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    @Value("${hibernate.dialect}")
    protected String hibernateDialect;

    protected static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    @Value("${hibernate.format_sql}")
    protected String hibernateFormatSql;

    protected static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    @Value("${hibernate.ejb.naming_strategy}")
    protected String hibernateNamingStrategy;

    protected static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    @Value("${hibernate.show_sql}")
    protected String hibernateShowSql;

    protected abstract String getDatabaseName();

    protected abstract String getEntitymanagerPackagesToScan();

    /**
     * Default constructor
     */
    public ApplicationPersistenceConfiguration() {
        // Default constructor
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    protected DataSource makeDataSource() {

        final DefaultConnectionPoolFactory factory = new DefaultConnectionPoolFactory.Builder()
                .withUsername(this.databaseUsername).withPassword(this.databasePassword)
                .withDriverClassName(this.databaseDriver).withProtocol(this.databaseProtocol)
                .withDatabaseHost(this.databaseHostname).withDatabasePort(this.databasePort)
                .withDatabaseName(this.getDatabaseName()).withMinPoolSize(this.databaseMinPoolSize)
                .withMaxPoolSize(this.databaseMaxPoolSize).withAutoCommit(this.databaseAutoCommit)
                .withIdleTimeout(this.databaseIdleTimeout).build();

        final HikariDataSource hikariDataSource = factory.getDefaultConnectionPool();

        LOGGER.info("Connecting to database {} as {}", hikariDataSource.getJdbcUrl(), hikariDataSource.getUsername());

        return hikariDataSource;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    protected LocalContainerEntityManagerFactoryBean makeEntityManager(final String unitName,
            final DataSource dataSource) throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName(unitName);
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean
                .setPackagesToScan(this.getEntitymanagerPackagesToScan().split(REGEX_COMMA_WITH_OPTIONAL_WHITESPACE));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT, this.hibernateDialect);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, this.hibernateFormatSql);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, this.hibernateNamingStrategy);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, this.hibernateShowSql);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }
}
