// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityMgrCore",
    transactionManagerRef = "txMgrCore",
    basePackageClasses = {DeviceRepository.class})
public class CorePersistenceConfig extends ApplicationPersistenceConfiguration {

  @Value("${db.name.osgp_core}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.core}")
  private String entitymanagerPackagesToScan;

  public CorePersistenceConfig() {}

  /**
   * Method for creating the Data Source.
   *
   * @return DataSource
   */
  @Primary
  @Bean(name = "dsCore")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Primary
  @Bean(name = "entityMgrCore")
  public LocalContainerEntityManagerFactoryBean entityMgrCore(
      @Qualifier("dsCore") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_CORE", dataSource);
  }

  @Override
  protected String getDatabaseName() {
    return this.databaseName;
  }

  @Override
  protected String getEntitymanagerPackagesToScan() {
    return this.entitymanagerPackagesToScan;
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   * @throws ClassNotFoundException when class not found
   */
  @Primary
  @Bean(name = "txMgrCore")
  public JpaTransactionManager txMgrCore(
      @Qualifier("entityMgrCore") final EntityManagerFactory entityManagerFactory)
      throws ClassNotFoundException {

    return new JpaTransactionManager(entityManagerFactory);
  }
}
