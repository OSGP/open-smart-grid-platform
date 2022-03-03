/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.opensmartgridplatform.ws.da.config.DistributionAutomationWebServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
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
      "org.opensmartgridplatform.domain.da",
      "org.opensmartgridplatform.adapter.ws.da",
      "org.opensmartgridplatform.domain.logging",
      "org.opensmartgridplatform.domain.core.services",
      "org.opensmartgridplatform.adapter.ws.shared.services",
      "org.opensmartgridplatform.shared.application.config"
    })
@ImportResource("classpath:applicationContext.xml")
@Import({DistributionAutomationWebServiceConfig.class, MetricsConfig.class})
@PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsDistributionAutomation/config}",
    ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

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
