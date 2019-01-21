/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
//import org.jboss.netty.logging.InternalLoggerFactory;
//import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Persistence configuration for the osgp_adapter_protocol_iec60870 database.
 */
@EnableJpaRepositories(entityManagerFactoryRef = "iec60870EntityManagerFactory", basePackageClasses = {
        Iec60870DeviceRepository.class })
@Configuration
@EnableTransactionManagement()
public class Iec60870PersistenceConfig extends AbstractPersistenceConfig {

    @Value("${db.username.iec60870}")
    private String databaseUsername;
    @Value("${db.password.iec60870}")
    private String databasePassword;

    @Value("${db.host.iec60870}")
    private String databaseHost;
    @Value("${db.port.iec60870}")
    private int databasePort;
    @Value("${db.name.iec60870}")
    private String databaseName;

    private HikariDataSource dataSourceIec60870;

    public Iec60870PersistenceConfig() {
        // TODO: is onderstaande regel nodig? Zo ja, waarvoor? Voor logging door
        // netty?
        // InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    public DataSource getDataSourceIec60870() {
        if (this.dataSourceIec60870 == null) {
            final DefaultConnectionPoolFactory.Builder builder = super.builder().withUsername(this.databaseUsername)
                    .withPassword(this.databasePassword).withDatabaseHost(this.databaseHost)
                    .withDatabasePort(this.databasePort).withDatabaseName(this.databaseName);
            final DefaultConnectionPoolFactory factory = builder.build();
            this.dataSourceIec60870 = factory.getDefaultConnectionPool();
        }
        return this.dataSourceIec60870;
    }

    @Override
    @Bean
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Bean(initMethod = "migrate")
    public Flyway iec60870Flyway() {
        return super.createFlyway(this.getDataSourceIec60870());
    }

    @Override
    @Bean(name = "iec60870EntityManagerFactory")
    @DependsOn("iec60870Flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_PROTOCOL_ADAPTER_IEC60870", this.getDataSourceIec60870());
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        if (this.dataSourceIec60870 != null) {
            this.dataSourceIec60870.close();
        }
    }
}
