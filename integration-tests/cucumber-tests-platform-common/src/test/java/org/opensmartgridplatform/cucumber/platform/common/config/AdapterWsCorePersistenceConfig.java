/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.repositories.WsCoreNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityMgrWsCore",
    transactionManagerRef = "txMgrWsCore",
    basePackageClasses = {
      WsCoreNotificationWebServiceConfigurationRepository.class,
      ResponseDataRepository.class
    })
public class AdapterWsCorePersistenceConfig extends ApplicationPersistenceConfiguration {

  @Value("${db.name.osgp_adapter_ws_core}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.osgp_adapter_ws_core}")
  private String entitymanagerPackagesToScan;

  public AdapterWsCorePersistenceConfig() {}

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
  @Bean(name = "dsWsCore")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrWsCore")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dsWsCore") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_WS_CORE", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean(name = "txMgrWsCore")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityMgrWsCore") final EntityManagerFactory barEntityManagerFactory) {
    return new JpaTransactionManager(barEntityManagerFactory);
  }
}
