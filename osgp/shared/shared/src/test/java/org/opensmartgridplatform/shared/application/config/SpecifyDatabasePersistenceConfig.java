/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.application.config;

import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
// this file MUST be addressed as an external file, NOT on the classpath, otherwise it won't take
// precedence over global.properties
@PropertySource("file:target/test-classes/specify-database.properties")
@PropertySource("classpath:global-url.properties")
public class SpecifyDatabasePersistenceConfig extends AbstractPersistenceConfig {

  @Override
  @Bean("testBuilder")
  protected Builder builder() {
    return super.builder();
  }

  @Override
  protected LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory("test");
  }
}
