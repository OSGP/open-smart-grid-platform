/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.db.application.config;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.exceptions.SharedDbException;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractCustomConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@EnableJpaRepositories(entityManagerFactoryRef = "writableEntityManagerFactory", basePackageClasses = {
        WritableDeviceRepository.class })
@Configuration
@EnableTransactionManagement()
public class WritablePersistenceConfig extends AbstractCustomConfig {

    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.writable.username";
    private static final String PROPERTY_NAME_DATABASE_PW = "db.writable.password";

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
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.physical_naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

    private HikariDataSource dataSource;

    /**
     * Wire property sources to local environment.
     *
     * @throws IOException
     *             when required property source is not found.
     */
    @PostConstruct
    protected void init() throws IOException {
        this.addPropertySource("file:${osgp/AdapterWsSharedDb/config}", true);
        this.addPropertySource("file:${osgp/Global/config}", true);
        this.addPropertySource("classpath:osgp-adapter-ws-shared-db.properties", false);
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    public DataSource getWritableDataSource() {
        if (this.dataSource == null) {
            final String username = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME);
            final String password = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PW);

            final String driverClassName = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER);
            final String databaseProtocol = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PROTOCOL);

            final String databaseHost = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_HOST);
            final int databasePort = Integer.parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_PORT));
            final String databaseName = ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_NAME);

            final int minPoolSize = Integer
                    .parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_MIN_POOL_SIZE));
            final int maxPoolSize = Integer
                    .parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE));
            final boolean isAutoCommit = Boolean
                    .parseBoolean(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT));
            final int idleTimeout = Integer
                    .parseInt(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_DATABASE_IDLE_TIMEOUT));

            final DefaultConnectionPoolFactory.Builder builder = new DefaultConnectionPoolFactory.Builder()
                    .withUsername(username).withPassword(password).withDriverClassName(driverClassName)
                    .withProtocol(databaseProtocol).withDatabaseHost(databaseHost).withDatabasePort(databasePort)
                    .withDatabaseName(databaseName).withMinPoolSize(minPoolSize).withMaxPoolSize(maxPoolSize)
                    .withAutoCommit(isAutoCommit).withIdleTimeout(idleTimeout);
            final DefaultConnectionPoolFactory factory = builder.build();
            this.dataSource = factory.getDefaultConnectionPool();
        }
        return this.dataSource;
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws SharedDbException
     *             When creating entity manager factory fails.
     */
    @Bean
    public JpaTransactionManager writableTransactionManager() throws SharedDbException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        try {
            transactionManager.setEntityManagerFactory(this.writableEntityManagerFactory().getObject());
            transactionManager.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);
        } catch (final Exception e) {
            final String msg = "Error in creating transaction manager bean";
            throw new SharedDbException(msg, e);
        }

        return transactionManager;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean writableEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSGP_CORE_DB_API");
        entityManagerFactoryBean.setDataSource(this.getWritableDataSource());
        entityManagerFactoryBean
                .setPackagesToScan(ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
                ENVIRONMENT.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
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
