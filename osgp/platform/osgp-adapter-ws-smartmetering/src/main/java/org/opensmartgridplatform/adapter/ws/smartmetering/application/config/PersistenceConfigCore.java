// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableJpaRepositories(
    entityManagerFactoryRef = "coreEntityManagerFactory",
    basePackageClasses = {DeviceRepository.class})
@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class PersistenceConfigCore extends AbstractPersistenceConfig {

  @Value("${db.username.core}")
  private String username;

  @Value("${db.password.core}")
  private String password;

  @Value("${db.host.core}")
  private String databaseHost;

  @Value("${db.port.core}")
  private int databasePort;

  @Value("${db.name.core}")
  private String databaseName;

  @Value("${entitymanager.packages.to.scan.core}")
  private String entitymanagerPackagesToScan;

  private HikariDataSource dataSourceCore;

  @Bean(destroyMethod = "close")
  public DataSource getDataSourceCore() {

    if (this.dataSourceCore == null) {

      final DefaultConnectionPoolFactory.Builder builder =
          super.builder()
              .withUsername(this.username)
              .withPassword(this.password)
              .withDatabaseHost(this.databaseHost)
              .withDatabasePort(this.databasePort)
              .withDatabaseName(this.databaseName);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSourceCore = factory.getDefaultConnectionPool();
    }

    return this.dataSourceCore;
  }

  @Override
  @Bean(name = "coreTransactionManager")
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Override
  @Bean(name = "coreEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

    return super.entityManagerFactory(
        "OSGP_WS_ADAPTER_SMARTMETERING",
        this.getDataSourceCore(),
        this.entitymanagerPackagesToScan);
  }
}
