/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.config;

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

import org.opensmartgridplatform.cucumber.platform.config.CorePersistenceConfig;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.microgrids.repositories.RtuDeviceRepository;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrCoreMicrogrids", transactionManagerRef = "txMgrCoreMicrogrids", basePackageClasses = {
        DeviceRepository.class, RtuDeviceRepository.class })
public class CoreMicrogridsPersistenceConfig extends CorePersistenceConfig {

    @Value("${db.name.osgp_core}")
    private String databaseName;

    @Value("${entitymanager.packages.to.scan.core}")
    private String entitymanagerPackagesToScan;

    public CoreMicrogridsPersistenceConfig() {
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    @Override
    @Primary
    @Bean(name = "dsCoreMicrogrids")
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
    @Bean(name = "entityMgrCoreMicrogrids")
    public LocalContainerEntityManagerFactoryBean entityMgrCoreMicrogrids(
            @Qualifier("dsCoreMicrogrids") final DataSource dataSource) throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_CORE_MICROGRIDS", dataSource);
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
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Primary
    @Bean(name = "txMgrCoreMicrogrids")
    public JpaTransactionManager txMgrCoreMicrogrids(
            @Qualifier("entityMgrCoreMicrogrids") final EntityManagerFactory entityManagerFactory)
            throws ClassNotFoundException {

        return new JpaTransactionManager(entityManagerFactory);
    }

}
