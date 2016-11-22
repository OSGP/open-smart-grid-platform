/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.application.config;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.core.db.api.exceptions.CoreDbApiException;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;
import com.alliander.osgp.shared.application.config.AbstractCustomConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@EnableJpaRepositories(entityManagerFactoryRef = "osgpCoreDbApiEntityManagerFactory", basePackageClasses = { DeviceDataRepository.class })
@Configuration
@EnableTransactionManagement()
public class OsgpCoreDbApiPersistenceConfig extends AbstractCustomConfig {

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.api.driver";
    private static final String PROPERTY_NAME_DATABASE_PW = "db.api.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.api.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.api.username";

    private static final String PROPERTY_NAME_DATABASE_MAX_POOL_SIZE = "db.max_pool_size";
    private static final String PROPERTY_NAME_DATABASE_AUTO_COMMIT = "db.auto_commit";

    private static final String HIBERNATE_DIALECT_KEY = "hibernate.dialect";
    private static final String HIBERNATE_FORMAT_SQL_KEY = "hibernate.format_sql";
    private static final String HIBERNATE_NAMING_STRATEGY_KEY = "hibernate.ejb.naming_strategy";
    private static final String HIBERNATE_SHOW_SQL_KEY = "hibernate.show_sql";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT_VALUE = "api.hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL_VALUE = "api.hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY_VALUE = "api.hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL_VALUE = "api.hibernate.show_sql";

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "api.entitymanager.packages.to.scan";

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreDbApiPersistenceConfig.class);

    private HikariDataSource dataSource;

    /**
     * Wire property sources to local environment.
     * @throws IOException when required property source is not found.
     */
    @PostConstruct
    protected void init() throws IOException {
        addPropertySource("file:${osgp/CoreDbApi/config}",  true);
        addPropertySource("file:${osgp/Global/config}", true);
        addPropertySource("classpath:osgp-core-db-api.properties", false);        
    }
    
    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    public DataSource getOsgpCoreDbApiDataSource() {
        if (this.dataSource == null) {
            final HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setDriverClassName(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
            hikariConfig.setJdbcUrl(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
            hikariConfig.setUsername(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
            hikariConfig.setPassword(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PW));

            hikariConfig.setMaximumPoolSize(Integer.parseInt(ENVIRONMENT
                    .getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE)));
            hikariConfig.setAutoCommit(Boolean.parseBoolean(ENVIRONMENT
                    .getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT)));

            this.dataSource = new HikariDataSource(hikariConfig);
        }
        return this.dataSource;
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    public JpaTransactionManager osgpCoreDbApiTransactionManager() throws CoreDbApiException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        try {
            transactionManager.setEntityManagerFactory(this.osgpCoreDbApiEntityManagerFactory().getObject());
            transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ALWAYS);
        } catch (final ClassNotFoundException e) {
            final String msg = "Error in creating transaction manager bean";
            LOGGER.error(msg, e);
            throw new CoreDbApiException(msg, e);
        }

        return transactionManager;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean osgpCoreDbApiEntityManagerFactory() throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSGP_CORE_DB_API");
        entityManagerFactoryBean.setDataSource(this.getOsgpCoreDbApiDataSource());
        entityManagerFactoryBean.setPackagesToScan(ENVIRONMENT
                .getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(HIBERNATE_DIALECT_KEY,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT_VALUE));
        jpaProperties.put(HIBERNATE_FORMAT_SQL_KEY,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL_VALUE));
        jpaProperties.put(HIBERNATE_NAMING_STRATEGY_KEY,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY_VALUE));
        jpaProperties.put(HIBERNATE_SHOW_SQL_KEY,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL_VALUE));

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
