/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.config;

import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.domain.core.exceptions.PlatformException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class AbstractPersistenceConfigBase {

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";

    private static final String PROPERTY_NAME_DATABASE_MAX_POOL_SIZE = "db.max_pool_size";
    private static final String PROPERTY_NAME_DATABASE_AUTO_COMMIT = "db.auto_commit";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    @Autowired
    protected Environment environment;

    protected final Logger logger;
    private final String usernameProperty;
    private final String passwordProperty;
    private final String urlProperty;
    private final String packagesToScanProperty;
    private final String persistenceUnit;

    private HikariDataSource dataSource;

    public AbstractPersistenceConfigBase(final String persistenceUnit, final String usernameProperty,
            final String passwordProperty, final String urlProperty, final String packagesToScanProperty,
            final Class<?> loggerClass) {
        this.logger = LoggerFactory.getLogger(loggerClass);
        this.usernameProperty = usernameProperty;
        this.passwordProperty = passwordProperty;
        this.urlProperty = urlProperty;
        this.packagesToScanProperty = packagesToScanProperty;
        this.persistenceUnit = persistenceUnit;
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            final HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setDriverClassName(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
            hikariConfig.setJdbcUrl(this.environment.getRequiredProperty(this.urlProperty));
            hikariConfig.setUsername(this.environment.getRequiredProperty(this.usernameProperty));
            hikariConfig.setPassword(this.environment.getRequiredProperty(this.passwordProperty));

            hikariConfig.setMaximumPoolSize(
                    Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE)));
            hikariConfig.setAutoCommit(
                    Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT)));

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
    protected JpaTransactionManager createTransactionManager() throws PlatformException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        try {
            transactionManager.setEntityManagerFactory(this.createEntityManagerFactory().getObject());
            transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ALWAYS);
            transactionManager.setPersistenceUnitName(this.persistenceUnit);
        } catch (final ClassNotFoundException e) {
            final String msg = "Error in creating transaction manager bean";
            this.logger.error(msg, e);
            throw new PlatformException(msg, e);
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
    protected LocalContainerEntityManagerFactoryBean createEntityManagerFactory() throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName(this.persistenceUnit);
        entityManagerFactoryBean.setDataSource(this.getDataSource());
        entityManagerFactoryBean
                .setPackagesToScan(this.environment.getRequiredProperty(this.packagesToScanProperty).split(","));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
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
