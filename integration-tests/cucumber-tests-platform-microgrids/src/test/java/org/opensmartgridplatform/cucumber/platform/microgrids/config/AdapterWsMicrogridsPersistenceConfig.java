// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.opensmartgridplatform.cucumber.platform.config.ApplicationPersistenceConfiguration;
import org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.ws.WsMicrogridsResponseDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityMgrFactWsMicrogrids",
    transactionManagerRef = "txMgrWsMicrogrids",
    basePackageClasses = {WsMicrogridsResponseDataRepository.class})
public class AdapterWsMicrogridsPersistenceConfig extends ApplicationPersistenceConfiguration {

  public AdapterWsMicrogridsPersistenceConfig() {}

  @Value("${db.name.osgp_adapter_ws_microgrids}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.ws.microgrids}")
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
  @Bean(name = "dsWsMicrogrids")
  public DataSource dataSource() {
    return this.makeDataSource();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   * @throws ClassNotFoundException when class not found
   */
  @Bean(name = "entityMgrFactWsMicrogrids")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dsWsMicrogrids") final DataSource dataSource) throws ClassNotFoundException {

    return this.makeEntityManager("OSGP_CUCUMBER_WS_MICROGRIDS", dataSource);
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean(name = "txMgrWsMicrogrids")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityMgrFactWsMicrogrids") final EntityManagerFactory barEntityManagerFactory) {
    return new JpaTransactionManager(barEntityManagerFactory);
  }
}
