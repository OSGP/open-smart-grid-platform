/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
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
    entityManagerFactoryRef = "entityMgrFactIec60870",
    transactionManagerRef = "txMgrIec60870",
    basePackageClasses = {Iec60870DeviceRepository.class})
public class Iec60870PersistenceConfig extends ApplicationPersistenceConfiguration {

  public Iec60870PersistenceConfig() {}

  @Value("${iec60870.db.name.osgp.adapter.protocol}")
  private String databaseName;

  @Value("${iec60870.entitymanager.packages.to.scan}")
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
  @Bean(name = "dsIec60870")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrFactIec60870")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dsIec60870") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_IEC60870", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean(name = "txMgrIec60870")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityMgrFactIec60870") final EntityManagerFactory barEntityManagerFactory) {
    return new JpaTransactionManager(barEntityManagerFactory);
  }
}
