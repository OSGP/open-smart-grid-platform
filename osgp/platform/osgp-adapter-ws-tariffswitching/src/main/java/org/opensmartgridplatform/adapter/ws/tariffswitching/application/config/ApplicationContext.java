/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.ws.tariffswitching.config.TariffSwitchingWebServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * An application context Java configuration class. The usage of Java configuration requires Spring
 * Framework 3.0
 */
@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.shared.domain.services",
      "org.opensmartgridplatform.domain.core",
      "org.opensmartgridplatform.adapter.ws.tariffswitching",
      "org.opensmartgridplatform.adapter.ws.shared.services",
      "org.opensmartgridplatform.adapter.ws.mapping",
      "org.opensmartgridplatform.shared.application.config"
    })
@EnableTransactionManagement()
@ImportResource("classpath:applicationContext.xml")
@Import({
  PersistenceConfigCore.class,
  MessagingConfig.class,
  WebServiceConfig.class,
  TariffSwitchingWebServiceConfig.class
})
@PropertySource("classpath:osgp-adapter-ws-tariffswitching.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsTariffSwitching/config}",
    ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

  @Bean
  public LocalValidatorFactoryBean validator() {
    LOGGER.debug("Initializing Local Validator Factory Bean");
    final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
    final org.springframework.core.io.Resource[] resources = {
      new ClassPathResource("constraint-mappings.xml")
    };
    localValidatorFactoryBean.setMappingLocations(resources);
    return localValidatorFactoryBean;
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    LOGGER.debug("Initializing Method Validation Post Processor Bean");

    final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
    m.setValidatorFactory(this.validator());
    return m;
  }
}
