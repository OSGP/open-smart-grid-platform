/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.shared.application.config.AbstractPersistenceConfig;
import com.alliander.osgp.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

@EnableJpaRepositories(transactionManagerRef = "coreTransactionManager", entityManagerFactoryRef = "coreEntityManagerFactory", basePackageClasses = {
        com.alliander.osgp.domain.core.repositories.DeviceRepository.class,
        com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository.class })
@Configuration
@PropertySource("classpath:osgp-adapter-ws-microgrids.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsMicrogrids/config}", ignoreResourceNotFound = true)
public class PersistenceConfigCore extends AbstractPersistenceConfig {

    @Value("${db.username.core}")
    private String username;

    @Value("${db.password.core}")
    private String password;

    @Value("${db.host.core}")
    private String databaseHost;

    @Value("${db.port.core}")
    private int databasePort;

    @Value("${db.name.core}")
    private String databaseName;

    @Value("${entitymanager.packages.to.scan.core}")
    private String entitymanagerPackagesToScan;

    private HikariDataSource dataSourceCore;

    public PersistenceConfigCore() {
        // empty constructor
    }

    private DataSource getDataSourceCore() {

        if (this.dataSourceCore == null) {

            final DefaultConnectionPoolFactory.Builder builder = super.builder().withUsername(this.username)
                    .withPassword(this.password).withDatabaseHost(this.databaseHost).withDatabasePort(this.databasePort)
                    .withDatabaseName(this.databaseName);
            final DefaultConnectionPoolFactory factory = builder.build();
            this.dataSourceCore = factory.getDefaultConnectionPool();
        }

        return this.dataSourceCore;
    }

    @Override
    @Bean(name = "coreTransactionManager")
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @Bean(name = "coreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_CORE_MICROGRIDS", this.getDataSourceCore(),
                this.entitymanagerPackagesToScan);
    }
}
