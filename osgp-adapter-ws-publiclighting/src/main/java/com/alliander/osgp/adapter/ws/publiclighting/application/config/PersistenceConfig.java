/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.application.config;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.shared.application.config.AbstractPersistenceConfig;
import com.alliander.osgp.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", basePackageClasses = {
        DeviceRepository.class })
@Configuration
@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsPublicLighting/config}", ignoreResourceNotFound = true)
public class PersistenceConfig extends AbstractPersistenceConfig {

    @Value("${db.readonly.username}")
    private String username;

    @Value("${db.readonly.password}")
    private String password;

    @Value("${db.host}")
    private String databaseHost;

    @Value("${db.port}")
    private int databasePort;

    @Value("${db.name}")
    private String databaseName;

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
    @Bean
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        return super.entityManagerFactory("OSGP_WS_ADAPTER_PUBLICLIGHTING", this.getDataSourceCore());
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        if (this.dataSourceCore != null) {
            this.dataSourceCore.close();
        }
    }
}
