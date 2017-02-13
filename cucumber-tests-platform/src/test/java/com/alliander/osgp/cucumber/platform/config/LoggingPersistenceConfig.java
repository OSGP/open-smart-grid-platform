/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrLogging", transactionManagerRef = "txMgrLogging", basePackageClasses = { DeviceLogItemRepository.class })
public class LoggingPersistenceConfig extends ApplicationPersistenceConfiguration {

    @Value("${osgploggingdbs.url}")
    private String databaseUrl;

    @Value("${entitymanager.packages.to.scan.logging}")
    private String entitymanagerPackagesToScan;

    public LoggingPersistenceConfig() {
    }

    @Override
    protected String getDatabaseUrl() {
        return this.databaseUrl;
    }

    @Override
    protected String getEntitymanagerPackagesToScan() {
        return this.entitymanagerPackagesToScan;
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    @Primary
    @Bean(name = "dsLogging")
    public DataSource dataSource() {
        return this.makeDataSource();
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Primary
    @Bean(name = "entityMgrLogging")
    public LocalContainerEntityManagerFactoryBean entityMgrCore(@Qualifier("dsLogging") final DataSource dataSource)
            throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_LOGGING", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Primary
    @Bean(name = "txMgrLogging")
    public JpaTransactionManager txMgrCore(@Qualifier("entityMgrLogging") final EntityManagerFactory entityMgrCore)
            throws ClassNotFoundException {

        return new JpaTransactionManager(entityMgrCore);
    }

}
