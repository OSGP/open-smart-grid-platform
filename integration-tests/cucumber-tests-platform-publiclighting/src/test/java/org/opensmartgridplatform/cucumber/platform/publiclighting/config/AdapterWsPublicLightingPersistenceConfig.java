/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.ws.PublicLightingNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.ws.PublicLightingResponseDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityMgrFactWsPublicLighting",
    transactionManagerRef = "txMgrWsPublicLighting",
    basePackageClasses = {
      PublicLightingResponseDataRepository.class,
      PublicLightingNotificationWebServiceConfigurationRepository.class
    })
public class AdapterWsPublicLightingPersistenceConfig extends ApplicationPersistenceConfiguration {

  public AdapterWsPublicLightingPersistenceConfig() {}

  @Value("${db.name.osgp_adapter_ws_publiclighting}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.ws.publiclighting}")
  private String entitymanagerPackagesToScan;

  @Override
  protected String getDatabaseName() {
    return this.databaseName;
  }

  @Override
  protected String getEntitymanagerPackagesToScan() {
    return this.entitymanagerPackagesToScan;
  }

  /**
   * Method for creating the Data Source.
   *
   * @return DataSource
   */
  @Bean(name = "dsWsPublicLighting")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrFactWsPublicLighting")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dsWsPublicLighting") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_WS_PUBLIC_LIGHTING", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean(name = "txMgrWsPublicLighting")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityMgrFactWsPublicLighting")
          final EntityManagerFactory barEntityManagerFactory) {
    return new JpaTransactionManager(barEntityManagerFactory);
  }
}
