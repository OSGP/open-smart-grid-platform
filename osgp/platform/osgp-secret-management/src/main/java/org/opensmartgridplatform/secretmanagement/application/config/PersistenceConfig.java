/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.config;

import javax.annotation.PreDestroy;
import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactory",
    basePackageClasses = {DbEncryptedSecretRepository.class, DbEncryptionKeyRepository.class})
@Configuration
@PropertySource("classpath:osgp-secret-management.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SecretManagement/config}", ignoreResourceNotFound = true)
public class PersistenceConfig extends AbstractPersistenceConfig {

  @Override
  @Bean
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Override
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory("OSGP_SECRET_MANAGEMENT");
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    super.destroyDataSource();
  }

  @Bean(initMethod = "migrate")
  public Flyway flyway() {
    return super.createFlyway();
  }
}
