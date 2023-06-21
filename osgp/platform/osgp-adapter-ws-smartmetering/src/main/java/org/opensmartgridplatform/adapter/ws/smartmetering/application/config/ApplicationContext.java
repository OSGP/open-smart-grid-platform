// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import org.opensmartgridplatform.adapter.ws.infra.specifications.JpaEventSpecifications;
import org.opensmartgridplatform.domain.core.specifications.EventSpecifications;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.opensmartgridplatform.ws.smartmetering.config.SmartmeteringWebServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/** An application context Java configuration class. */
@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.shared.domain.services",
      "org.opensmartgridplatform.domain.core",
      "org.opensmartgridplatform.adapter.ws.smartmetering",
      "org.opensmartgridplatform.logging.domain",
      "org.opensmartgridplatform.adapter.ws.shared.services",
      "org.opensmartgridplatform.adapter.ws.mapping",
      "org.opensmartgridplatform.shared.application.config"
    })
@EnableTransactionManagement()
@ImportResource("classpath:applicationContext.xml")
@Import({
  PersistenceConfigWs.class,
  PersistenceConfigCore.class,
  MessagingConfig.class,
  WebServiceConfig.class,
  SmartmeteringWebServiceConfig.class,
  MetricsConfig.class
})
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
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

  @Bean
  public EventSpecifications eventSpecifications() {
    return new JpaEventSpecifications();
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    final PropertySourcesPlaceholderConfigurer propertySource =
        new PropertySourcesPlaceholderConfigurer();
    propertySource.setIgnoreUnresolvablePlaceholders(true);
    return propertySource;
  }

  @Bean
  public PagingSettings pagingSettings(
      @Value("${paging.maximum.pagesize}") final int maximumPageSize,
      @Value("${paging.default.pagesize}") final int defaultPageSize) {
    return new PagingSettings(maximumPageSize, defaultPageSize);
  }
}
