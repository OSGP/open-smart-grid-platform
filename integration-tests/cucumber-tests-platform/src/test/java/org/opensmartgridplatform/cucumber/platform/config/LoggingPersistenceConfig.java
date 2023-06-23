// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityMgrLogging",
    transactionManagerRef = "txMgrLogging",
    basePackageClasses = {DeviceLogItemPagingRepository.class})
public class LoggingPersistenceConfig extends ApplicationPersistenceConfiguration {

  @Value("${db.name.osgp_logging}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.logging}")
  private String entitymanagerPackagesToScan;

  public LoggingPersistenceConfig() {}

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
  @Bean(name = "dsLogging")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrLogging")
  public LocalContainerEntityManagerFactoryBean entityMgrCore(
      @Qualifier("dsLogging") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_LOGGING", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "txMgrLogging")
  public JpaTransactionManager txMgrCore(
      @Qualifier("entityMgrLogging") final EntityManagerFactory entityMgrCore)
      throws ClassNotFoundException {

    return new JpaTransactionManager(entityMgrCore);
  }
}
