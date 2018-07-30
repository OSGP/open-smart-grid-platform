/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.smartmetering.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrRespData", transactionManagerRef = "txMgrRespData", basePackageClasses = {
        ResponseUrlDataRepository.class, ResponseDataRepository.class })
public class AdapterWsSmartMeteringPersistenceConfig extends ApplicationPersistenceConfiguration {

    @Value("${db.name.osgp_adapter_ws_smartmetering}")
    private String databaseName;

    @Value("${entitymanager.packages.to.scan}")
    private String entitymanagerPackagesToScan;

    public AdapterWsSmartMeteringPersistenceConfig() {
    }

    @Override
    protected String getDatabaseName() {
        return this.databaseName;
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
    @Bean(name = "dsRespData")
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
    @Bean(name = "entityMgrRespData")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dsRespData") final DataSource dataSource) throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_RESPDATA", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     */
    @Bean(name = "txMgrRespData")
    public JpaTransactionManager transactionManager(
            @Qualifier("entityMgrRespData") final EntityManagerFactory barEntityManagerFactory) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }

}
