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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrFactIec61850", transactionManagerRef = "txMgrIec61850", basePackageClasses = {
        Iec61850DeviceRepository.class })
public class AdapterProtocolIec61850PersistenceConfig extends ApplicationPersistenceConfiguration {

    public AdapterProtocolIec61850PersistenceConfig() {
    }

    @Value("${db.name.osgp_adapter_protocol_iec61850}")
    private String databaseName;

    @Value("${entitymanager.packages.to.scan.iec61850}")
    private String entitymanagerPackagesToScan;

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
    @Bean(name = "dsIec61850")
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
    @Bean(name = "entityMgrFactIec61850")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dsIec61850") final DataSource dataSource) throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_IEC61850", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean(name = "txMgrIec61850")
    public JpaTransactionManager transactionManager(
            @Qualifier("entityMgrFactIec61850") final EntityManagerFactory barEntityManagerFactory) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }

}
