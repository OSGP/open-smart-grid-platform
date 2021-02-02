/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.opensmartgridplatform.adapter.kafka.da.domain.repositories.LocationRepository;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrFactKafkaDistributionAutomation",
        transactionManagerRef = "txMgrKafkaDistributionAutomation",
        basePackageClasses = { LocationRepository.class })
public class AdapterKafkaDistributionAutomationPersistenceConfig extends ApplicationPersistenceConfiguration {

    public AdapterKafkaDistributionAutomationPersistenceConfig() {
    }

    @Value("${db.name.osgp_adapter_kafka_distributionautomation}")
    private String databaseName;

    @Value("${entitymanager.packages.to.scan.kafka.distributionautomation}")
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
    @Bean(name = "dsKafkaDistributionAutomation")
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
    @Bean(name = "entityMgrFactKafkaDistributionAutomation")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dsKafkaDistributionAutomation") final DataSource dataSource) throws ClassNotFoundException {

        return this.makeEntityManager("OSGP_CUCUMBER_KAFKA_DISTRIBUTION_AUTOMATION", dataSource);
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     */
    @Bean(name = "txMgrKafkaDistributionAutomation")
    public JpaTransactionManager transactionManager(
            @Qualifier("entityMgrFactKafkaDistributionAutomation") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
