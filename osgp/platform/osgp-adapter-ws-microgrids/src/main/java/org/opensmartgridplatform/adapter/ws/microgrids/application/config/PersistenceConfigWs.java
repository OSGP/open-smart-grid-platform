// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.application.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableJpaRepositories(
    transactionManagerRef = "transactionManager",
    entityManagerFactoryRef = "wsEntityManagerFactory",
    basePackageClasses = {
      org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository.class
    })
@Configuration
@PropertySource("classpath:osgp-adapter-ws-microgrids.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsMicrogrids/config}", ignoreResourceNotFound = true)
public class PersistenceConfigWs extends AbstractPersistenceConfig {

  public PersistenceConfigWs() {
    // Empty default constructor
  }

  @Bean(destroyMethod = "close")
  public DataSource dataSource() {
    return super.getDataSource();
  }

  @Override
  @Bean(name = "transactionManager")
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Override
  @DependsOn("flyway")
  @Bean(name = "wsEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory("OSGP_WS_ADAPTER_MICROGRIDS");
  }

  @Bean(initMethod = "migrate")
  public Flyway flyway() {
    return super.createFlyway();
  }
}
