// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.distributionautomation.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database.ws.WsDistributionAutomationResponseDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityMgrFactWsDistributionAutomation",
    transactionManagerRef = "txMgrWsDistributionAutomation",
    basePackageClasses = {WsDistributionAutomationResponseDataRepository.class})
public class AdapterWsDistributionAutomationPersistenceConfig
    extends ApplicationPersistenceConfiguration {

  public AdapterWsDistributionAutomationPersistenceConfig() {}

  @Value("${db.name.osgp_adapter_ws_distributionautomation}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.ws.distributionautomation}")
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
  @Bean(name = "dsWsDistributionAutomation")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrFactWsDistributionAutomation")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dsWsDistributionAutomation") final DataSource dataSource)
      throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_WS_DISTRIBUTION_AUTOMATION", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean(name = "txMgrWsDistributionAutomation")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityMgrFactWsDistributionAutomation")
          final EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
