/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.cucumber.platform.config.ApplicationPersistenceConfiguration;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrFactDlms", transactionManagerRef = "txMgrDlms", basePackageClasses = {
        DlmsDeviceRepository.class })
public class AdapterProtocolDlmsPersistenceConfig extends ApplicationPersistenceConfiguration {

    public AdapterProtocolDlmsPersistenceConfig() {
    }

    @Value("${db.name.osgp_adapter_protocol_dlms}")
    private String databaseName;

    @Value("${entitymanager.packages.to.scan.dlms}")
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
    @Bean(name = "dsDlms")
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
    @Bean(name = "entityMgrFactDlms")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dsDlms") final DataSource dataSource)
            throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_DLMS", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean(name = "txMgrDlms")
    public JpaTransactionManager transactionManager(
            @Qualifier("entityMgrFactDlms") final EntityManagerFactory barEntityManagerFactory) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }

}
