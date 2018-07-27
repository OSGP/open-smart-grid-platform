/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;

@EnableJpaRepositories(transactionManagerRef = "transactionManager", entityManagerFactoryRef = "wsEntityManagerFactory", basePackageClasses = {
        ResponseDataRepository.class })
@Configuration
@PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsDistributionAutomation/config}", ignoreResourceNotFound = true)
public class PersistenceConfigWs extends AbstractPersistenceConfig {

    public PersistenceConfigWs() {
        // Empty default constructor
    }

    @Override
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @DependsOn("flyway")
    @Bean(name = "wsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_WS_ADAPTER_DISTRIBUTION_AUTOMATION");
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return super.createFlyway();
    }
}
