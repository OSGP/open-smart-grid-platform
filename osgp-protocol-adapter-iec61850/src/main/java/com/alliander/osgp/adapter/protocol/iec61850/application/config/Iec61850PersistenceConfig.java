/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.config;

import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.ejb.HibernatePersistence;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.shared.application.config.AbstractConfig;
import com.alliander.osgp.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Persistence configuration for the osgp_adapter_protocol_iec61850 database.
 */
@EnableJpaRepositories(entityManagerFactoryRef = "iec61850EntityManagerFactory", basePackageClasses = { Iec61850DeviceRepository.class })
@Configuration
@EnableTransactionManagement()
public class Iec61850PersistenceConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850PersistenceConfig.class);

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    @Value("${db.username.iec61850}")
    private String databaseUsername;
    @Value("${db.password.iec61850}")
    private String databasePassword;

    @Value("${db.driver}")
    private String databaseDriver;
    @Value("${db.protocol}")
    private String databaseProtocol;

    @Value("${db.host.iec61850}")
    private String databaseHost;
    @Value("${db.port.iec61850}")
    private int databasePort;
    @Value("${db.name.iec61850}")
    private String databaseName;

    @Value("${db.min_pool_size}")
    private int databaseMinPoolSize;
    @Value("${db.max_pool_size}")
    private int databaseMaxPoolSize;
    @Value("${db.auto_commit}")
    private boolean databaseAutoCommit;
    @Value("${db.idle_timeout}")
    private int databaseIdleTimeout;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;
    @Value("${hibernate.format_sql}")
    private String hibernateFormatSql;
    @Value("${hibernate.ejb.naming_strategy}")
    private String hibernateNamingStrategy;
    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;

    @Value("${flyway.initial.version}")
    private String flywayInitialVersion;
    @Value("${flyway.initial.description}")
    private String flywayInitialDescription;
    @Value("${flyway.init.on.migrate}")
    private boolean flywayInitOnMigrate;

    @Value("${entitymanager.packages.to.scan}")
    private String entityManagerPackagesToScan;

    private HikariDataSource dataSource;

    public Iec61850PersistenceConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    public DataSource iec61850DataSource() {
        if (this.dataSource == null) {
            final DefaultConnectionPoolFactory.Builder builder = new DefaultConnectionPoolFactory.Builder()
            .withUsername(this.databaseUsername).withPassword(this.databasePassword)
            .withDriverClassName(this.databaseDriver).withProtocol(this.databaseProtocol)
            .withDatabaseHost(this.databaseHost).withDatabasePort(this.databasePort)
            .withDatabaseName(this.databaseName).withMinPoolSize(this.databaseMinPoolSize)
            .withMaxPoolSize(this.databaseMaxPoolSize).withAutoCommit(this.databaseAutoCommit)
            .withIdleTimeout(this.databaseIdleTimeout);
            final DefaultConnectionPoolFactory factory = builder.build();
            this.dataSource = factory.getDefaultConnectionPool();
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
    public JpaTransactionManager transactionManager() throws ProtocolAdapterException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        try {
            transactionManager.setEntityManagerFactory(this.iec61850EntityManagerFactory().getObject());
            transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ALWAYS);
        } catch (final ClassNotFoundException e) {
            final String msg = "Error in creating transaction manager bean";
            LOGGER.error(msg, e);
            throw new ProtocolAdapterException(msg, e);
        }

        return transactionManager;
    }

    /**
     * @return
     */
    @Bean(initMethod = "migrate")
    public Flyway iec61850Flyway() {
        final Flyway flyway = new Flyway();

        // Initialization for non-empty schema with no metadata table
        flyway.setBaselineVersion(MigrationVersion.fromVersion(this.flywayInitialVersion));
        flyway.setBaselineDescription(this.flywayInitialDescription);
        flyway.setBaselineOnMigrate(this.flywayInitOnMigrate);

        flyway.setDataSource(this.iec61850DataSource());

        return flyway;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    @DependsOn("iec61850Flyway")
    public LocalContainerEntityManagerFactoryBean iec61850EntityManagerFactory() throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSGP_PROTOCOL_ADAPTER_IEC61850");
        entityManagerFactoryBean.setDataSource(this.iec61850DataSource());
        entityManagerFactoryBean.setPackagesToScan(this.entityManagerPackagesToScan);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT, this.hibernateDialect);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, this.hibernateFormatSql);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, this.hibernateNamingStrategy);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, this.hibernateShowSql);

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
