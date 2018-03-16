/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.application.config;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;
import com.alliander.osgp.shared.application.config.AbstractPersistenceConfig;

@EnableJpaRepositories(basePackageClasses = { RtuDeviceRepository.class,
        DeviceRepository.class }, entityManagerFactoryRef = "entityManagerFactory")
@Configuration
@PropertySource("classpath:osgp-adapter-domain-microgrids.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainMicrogrids/config}", ignoreResourceNotFound = true)
public class PersistenceConfig extends AbstractPersistenceConfig {

    @Override
    @Bean
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        return super.entityManagerFactory("OSGP_DOMAIN_ADAPTER_MICROGRIDS");
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        super.destroyDataSource();
    }
}
