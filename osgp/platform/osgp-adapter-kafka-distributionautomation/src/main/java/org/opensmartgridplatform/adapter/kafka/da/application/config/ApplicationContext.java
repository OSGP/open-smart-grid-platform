/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ComponentScan("org.opensmartgridplatform.adapter.kafka.da")
@ComponentScan("org.opensmartgridplatform.domain.da")
@ComponentScan("org.opensmartgridplatform.domain.core.services")
@ComponentScan("org.opensmartgridplatform.domain.logging")
@ComponentScan("org.opensmartgridplatform.shared.application.config")
@ComponentScan("org.opensmartgridplatform.shared.domain.entities")
@ComponentScan("org.opensmartgridplatform.shared.domain.services")
@Import({PersistenceConfig.class, PersistenceConfigCore.class, MessagingConfig.class})
@PropertySource("classpath:osgp-adapter-kafka-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterKafkaDistributionAutomation/config}",
    ignoreResourceNotFound = true)
public class ApplicationContext {

  @Bean
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
    m.setValidatorFactory(this.validator());
    return m;
  }
}
