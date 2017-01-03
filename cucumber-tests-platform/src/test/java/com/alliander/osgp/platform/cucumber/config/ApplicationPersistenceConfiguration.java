/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * Base class for the application persistence configuration.
 */
@Configuration
public abstract class ApplicationPersistenceConfiguration extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPersistenceConfiguration.class);

    private static final String REGEX_COMMA_WITH_OPTIONAL_WHITESPACE = "\\s*+,\\s*+";

    @Value("${db.driver}")
    protected String databaseDriver;

    @Value("${db.username}")
    protected String databaseUsername;

    @Value("${db.password}")
    protected String databasePassword;

    @Value("${db.hostname}")
    protected String databaseHostname;

    @Value("${db.port}")
    protected String databasePort;

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

    protected abstract String getDatabaseUrl();

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

        final SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        singleConnectionDataSource.setAutoCommit(false);
        final Properties properties = new Properties();
        properties.setProperty("socketTimeout", "0");
        properties.setProperty("tcpKeepAlive", "true");

        singleConnectionDataSource.setDriverClassName(this.databaseDriver);
        singleConnectionDataSource.setUrl(this.getDatabaseUrl());
        singleConnectionDataSource.setUsername(this.databaseUsername);
        singleConnectionDataSource.setPassword(this.databasePassword);
        singleConnectionDataSource.setSuppressClose(true);

        LOGGER.info("Connecting to database {} as {}", singleConnectionDataSource.getUrl(),
                singleConnectionDataSource.getUsername());

        return singleConnectionDataSource;
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
