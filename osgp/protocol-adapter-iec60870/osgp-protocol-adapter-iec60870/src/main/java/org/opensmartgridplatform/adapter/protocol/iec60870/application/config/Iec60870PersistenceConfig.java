/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import javax.annotation.PreDestroy;
import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Persistence configuration for the osgp_adapter_protocol_iec60870 database. */
@EnableJpaRepositories(
    entityManagerFactoryRef = "iec60870EntityManagerFactory",
    basePackageClasses = {Iec60870DeviceRepository.class})
@Configuration
@EnableTransactionManagement()
public class Iec60870PersistenceConfig extends AbstractPersistenceConfig {

  @Override
  @Bean
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Bean(initMethod = "migrate")
  public Flyway iec60870Flyway() {
    return super.createFlyway(this.getDataSource());
  }

  @Override
  @Bean(name = "iec60870EntityManagerFactory")
  @DependsOn("iec60870Flyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory("OSGP_PROTOCOL_ADAPTER_IEC60870", this.getDataSource());
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    super.destroyDataSource();
  }
}
