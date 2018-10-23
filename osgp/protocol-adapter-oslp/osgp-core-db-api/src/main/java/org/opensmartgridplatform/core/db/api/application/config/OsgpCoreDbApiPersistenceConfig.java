/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.application.config;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.opensmartgridplatform.core.db.api.repositories.DeviceDataRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

@EnableJpaRepositories(entityManagerFactoryRef = "osgpCoreDbApiEntityManagerFactory", basePackageClasses = {
        DeviceDataRepository.class })
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-core-db-api.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/CoreDbApi/config}", ignoreResourceNotFound = true)
public class OsgpCoreDbApiPersistenceConfig extends AbstractPersistenceConfig {

    @Value("${db.api.username.oslp}")
    private String username;

    @Value("${db.api.password.oslp}")
    private String password;

    @Value("${db.api.host.oslp}")
    private String databaseHost;

    @Value("${db.api.port.oslp}")
    private int databasePort;

    @Value("${db.api.name.oslp}")
    private String databaseName;

    @Value("${api.entitymanager.packages.to.scan}")
    private String entitymanagerPackagesToScan;

    private HikariDataSource dataSourceCore;

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
    @Bean(name = "osgpCoreDbApiTransactionManager")
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @Bean(name = "osgpCoreDbApiEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_CORE_DB_API", this.getDataSourceCore(),
                this.entitymanagerPackagesToScan);
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        if (this.dataSourceCore != null) {
            this.dataSourceCore.close();
        }
    }
}
