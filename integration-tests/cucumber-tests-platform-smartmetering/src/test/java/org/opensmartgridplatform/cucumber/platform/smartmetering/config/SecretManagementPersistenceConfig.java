// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
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
    entityManagerFactoryRef = "entityMgrSecMgt",
    transactionManagerRef = "txMgrSecMgt",
    basePackageClasses = {DbEncryptedSecretRepository.class, DbEncryptionKeyRepository.class})
public class SecretManagementPersistenceConfig extends ApplicationPersistenceConfiguration {

  @Value("${db.name.secret_management}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.secret_management}")
  private String entitymanagerPackagesToScan;

  public SecretManagementPersistenceConfig() {}

  /**
   * Method for creating the Data Source.
   *
   * @return DataSource
   */
  @Primary
  @Bean(name = "dsSecMgt")
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
  @Bean(name = "entityMgrSecMgt")
  public LocalContainerEntityManagerFactoryBean entityMgrSecretManagement(
      @Qualifier("dsSecMgt") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_SEC_MGT", dataSource);
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
  @Bean(name = "txMgrSecMgt")
  public JpaTransactionManager txMgrSecretManagement(
      @Qualifier("entityMgrSecMgt") final EntityManagerFactory entityManagerFactory)
      throws ClassNotFoundException {

    return new JpaTransactionManager(entityManagerFactory);
  }
}
