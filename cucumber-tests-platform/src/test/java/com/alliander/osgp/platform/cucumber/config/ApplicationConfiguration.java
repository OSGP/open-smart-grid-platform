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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * Base class for the application configuration.
 */
@Configuration
@PropertySources({
    @PropertySource("classpath:cucumber-platform.properties"),
    @PropertySource(value = "file:/etc/osp/test/global-cucumber.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:/etc/osp/test/cucumber-platform.properties", ignoreResourceNotFound = true),
})
public abstract class ApplicationConfiguration extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

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

    @Value("${device.networkaddress}")
    public String deviceNetworkaddress;

    @Value("${iec61850.mock.networkaddress}")
    private String iec61850MockNetworkAddress;
    @Value("${iec61850.mock.icd.filename}")
    private String iec61850MockIcdFilename;
    @Value("${iec61850.mock.port}")
    private int iec61850MockPort;

    protected abstract String getDatabaseUrl();

    protected abstract String getEntitymanagerPackagesToScan();

    /**
     * Default constructor
     */
    public ApplicationConfiguration() {
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

    @Bean(name = "iec61850MockNetworkAddress")
    public String getIec61850MockNetworkAddress() {
        return this.iec61850MockNetworkAddress;
    }

    @Bean(name = "iec61850MockPort")
    public int getIec61850MockPort() {
        return this.iec61850MockPort;
    }

    @Bean(name = "iec61850MockIcdFilename")
    public String getIec61850MockIcdFilename() {
        return this.iec61850MockIcdFilename;
    }
}
