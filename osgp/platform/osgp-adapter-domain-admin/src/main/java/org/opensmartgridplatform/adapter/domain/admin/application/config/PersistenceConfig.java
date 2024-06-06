// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.admin.application.config;

import jakarta.annotation.PreDestroy;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactory",
    basePackageClasses = {DeviceRepository.class})
@Configuration
@PropertySource("classpath:osgp-adapter-domain-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainAdmin/config}", ignoreResourceNotFound = true)
public class PersistenceConfig extends AbstractPersistenceConfig {

  @Override
  @Bean
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Override
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory("OSGP_DOMAIN_ADAPTER_ADMIN");
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    super.destroyDataSource();
  }
}
