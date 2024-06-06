// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
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
    entityManagerFactoryRef = "entityMgrFactOslp",
    transactionManagerRef = "txMgrOslp",
    basePackageClasses = {OslpDeviceRepository.class})
public class AdapterProtocolOslpPersistenceConfig extends ApplicationPersistenceConfiguration {

  public AdapterProtocolOslpPersistenceConfig() {}

  @Value("${db.name.osgp_adapter_protocol_oslp}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.oslp}")
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
  @Bean(name = "dsOslp")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrFactOslp")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dsOslp") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_OSLP", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean(name = "txMgrOslp")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityMgrFactOslp") final EntityManagerFactory barEntityManagerFactory) {
    return new JpaTransactionManager(barEntityManagerFactory);
  }
}
