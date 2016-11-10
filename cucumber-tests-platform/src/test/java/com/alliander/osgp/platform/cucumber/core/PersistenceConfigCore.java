/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.core;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrCore",  
    transactionManagerRef = "txMgrCore",
    basePackageClasses = { DeviceRepository.class, DeviceAuthorizationRepository.class })
public class PersistenceConfigCore extends ApplicationConfiguration {

    @Value("${osgpcoredbs.url}")
    private String databaseUrl;

    @Value("${entitymanager.packages.to.scan.core}")
    private String entitymanagerPackagesToScan;
      
    public PersistenceConfigCore() {
    }

    @Override
    protected String getDatabaseUrl() {
        return databaseUrl;
    }

    @Override
    protected String getEntitymanagerPackagesToScan() {
        return entitymanagerPackagesToScan;
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    @Primary
    @Bean(name = "dsCore")    
    public DataSource dataSource() {
        return makeDataSource();
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
    public LocalContainerEntityManagerFactoryBean entityMgrCore(@Qualifier("dsCore") DataSource dataSource)
            throws ClassNotFoundException {

        return makeEntityManager("OSGP_CUCUMBER_CORE", dataSource);
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
            @Qualifier("entityMgrCore") EntityManagerFactory entityMgrCore) throws ClassNotFoundException {

        return new JpaTransactionManager(entityMgrCore);
    }

}
