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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;

@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrRespData", 
    transactionManagerRef = "txMgrRespData",
    basePackageClasses = { MeterResponseDataRepository.class })
public class PersistenceConfigResponseData extends ApplicationConfiguration {

    @Value("${osgpadapterwssmartmeteringdbs.url}")
    private String databaseUrl;

    @Value("${entitymanager.packages.to.scan}")
    private String entitymanagerPackagesToScan;

 
    public PersistenceConfigResponseData() {
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
    @Bean(name = "dsRespData")    
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
    @Bean(name = "entityMgrRespData")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dsRespData") DataSource dataSource) throws ClassNotFoundException {

        return makeEntityManager("OSGP_CUCUMBER_RESPDATA", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean(name = "txMgrRespData")    
    public JpaTransactionManager transactionManager(
            @Qualifier("entityMgrRespData") EntityManagerFactory barEntityManagerFactory) {
        return new JpaTransactionManager(barEntityManagerFactory);    }

}
