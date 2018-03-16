/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.shared.application.config.AbstractPersistenceConfig;
import com.alliander.osgp.shared.infra.db.DefaultConnectionPoolFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * An application context Java configuration class.
 */
@EnableJpaRepositories(entityManagerFactoryRef = "dlmsEntityManagerFactory", basePackageClasses = {
        DlmsDeviceRepository.class })
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class DlmsPersistenceConfig extends AbstractPersistenceConfig {

    @Value("${db.username.dlms}")
    private String username;

    @Value("${db.password.dlms}")
    private String password;

    @Value("${db.host.dlms}")
    private String databaseHost;

    @Value("${db.port.dlms}")
    private int databasePort;

    @Value("${db.name.dlms}")
    private String databaseName;

    private HikariDataSource dataSourceDlms;

    public DlmsPersistenceConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    public DataSource getDataSourceDlms() {
        if (this.dataSourceDlms == null) {
            final DefaultConnectionPoolFactory.Builder builder = super.builder().withUsername(this.username)
                    .withPassword(this.password).withDatabaseHost(this.databaseHost).withDatabasePort(this.databasePort)
                    .withDatabaseName(this.databaseName);
            final DefaultConnectionPoolFactory factory = builder.build();
            this.dataSourceDlms = factory.getDefaultConnectionPool();
        }
        return this.dataSourceDlms;
    }

    @Override
    @Bean
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Bean(initMethod = "migrate")
    public Flyway dlmsFlyway() {
        return super.createFlyway(this.getDataSourceDlms());
    }

    @Override
    @Bean(name = "dlmsEntityManagerFactory")
    @DependsOn("dlmsFlyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_PROTOCOL_ADAPTER_DLMS_SETTINGS", this.getDataSourceDlms());
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        if (this.dataSourceDlms != null) {
            this.dataSourceDlms.close();
        }
    }
}
