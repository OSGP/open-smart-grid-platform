/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;

@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrCore", transactionManagerRef = "txMgrCore", basePackageClasses = {
        DeviceRepository.class, RtuDeviceRepository.class })
public class CorePersistenceConfig extends ApplicationPersistenceConfiguration {

    @Value("${osgpcoredbs.url}")
    private String databaseUrl;

    @Value("${entitymanager.packages.to.scan.core}")
    private String entitymanagerPackagesToScan;

    public CorePersistenceConfig() {
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
    @Bean(name = "dsCore")
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
    @Bean(name = "entityMgrCore")
    public LocalContainerEntityManagerFactoryBean entityMgrCore(@Qualifier("dsCore") final DataSource dataSource)
            throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_CORE", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Primary
    @Bean(name = "txMgrCore")
    public JpaTransactionManager txMgrCore(
            @Qualifier("entityMgrCore") final EntityManagerFactory entityMgrCore) throws ClassNotFoundException {

        return new JpaTransactionManager(entityMgrCore);
    }

}
