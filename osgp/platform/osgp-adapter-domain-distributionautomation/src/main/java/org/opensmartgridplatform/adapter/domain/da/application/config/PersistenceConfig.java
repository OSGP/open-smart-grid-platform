/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.config;

import javax.annotation.PreDestroy;

import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(basePackageClasses = { RtuDeviceRepository.class, DeviceRepository.class })
@EnableTransactionManagement
@Configuration
public class PersistenceConfig extends AbstractPersistenceConfig {

    @Override
    @Bean
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_DOMAIN_ADAPTER_DISTRIBUTION_AUTOMATION");
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        super.destroyDataSource();
    }
}
