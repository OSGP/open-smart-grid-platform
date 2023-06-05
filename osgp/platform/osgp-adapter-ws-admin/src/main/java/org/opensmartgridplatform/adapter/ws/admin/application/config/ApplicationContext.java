// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.application.config;

import org.opensmartgridplatform.adapter.ws.infra.specifications.JpaDeviceSpecifications;
import org.opensmartgridplatform.adapter.ws.infra.specifications.JpaEventSpecifications;
import org.opensmartgridplatform.domain.core.specifications.DeviceSpecifications;
import org.opensmartgridplatform.domain.core.specifications.EventSpecifications;
import org.opensmartgridplatform.logging.domain.config.ReadOnlyLoggingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.opensmartgridplatform.ws.admin.config.AdminWebServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/** An application context Java configuration class. */
@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.shared.domain.services",
      "org.opensmartgridplatform.domain.core",
      "org.opensmartgridplatform.adapter.ws.admin",
      "org.opensmartgridplatform.logging.domain"
    })
@ImportResource("classpath:applicationContext.xml")
@Import({
  MessagingConfig.class,
  PersistenceConfig.class,
  WebServiceConfig.class,
  ReadOnlyLoggingConfig.class,
  AdminWebServiceConfig.class,
  MetricsConfig.class
})
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  private static final String PROPERTY_NAME_DEFAULT_PROTOCOL = "default.protocol";
  private static final String PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION = "default.protocol.version";

  private static final String PROPERTY_NAME_RECENT_DEVICES_PERIOD = "recent.devices.period";

  private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
  private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

  private static final String PROPERTY_NAME_NET_MANAGEMENT_ORGANISATION =
      "net.management.organisation";

  @Bean
  public String defaultProtocol() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_DEFAULT_PROTOCOL);
  }

  @Bean
  public String defaultProtocolVersion() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION);
  }

  @Bean
  public Integer recentDevicesPeriod() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_RECENT_DEVICES_PERIOD));
  }

  @Bean
  public PagingSettings pagingSettings() {
    return new PagingSettings(
        Integer.parseInt(
            this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE)),
        Integer.parseInt(
            this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE)));
  }

  @Bean
  public EventSpecifications eventSpecifications() {
    return new JpaEventSpecifications();
  }

  @Bean
  public DeviceSpecifications deviceSpecifications() {
    return new JpaDeviceSpecifications();
  }

  @Bean
  public LocalValidatorFactoryBean validator() {
    final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
    final org.springframework.core.io.Resource[] resources = {
      new ClassPathResource("constraint-mappings.xml")
    };
    localValidatorFactoryBean.setMappingLocations(resources);
    return localValidatorFactoryBean;
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
    m.setValidatorFactory(this.validator());
    return m;
  }

  @Bean
  public String netManagementOrganisation() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_NET_MANAGEMENT_ORGANISATION);
  }
}
