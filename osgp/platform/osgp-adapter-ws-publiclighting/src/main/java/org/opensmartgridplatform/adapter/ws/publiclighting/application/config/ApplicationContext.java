// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.opensmartgridplatform.ws.publiclighting.config.PublicLightingWebServiceConfig;
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
      "org.opensmartgridplatform.adapter.ws.publiclighting",
      "org.opensmartgridplatform.adapter.ws.shared.services",
      "org.opensmartgridplatform.adapter.ws.mapping",
      "org.opensmartgridplatform.shared.application.config"
    })
@EnableTransactionManagement()
@ImportResource("classpath:applicationContext.xml")
@Import({
  MessagingConfig.class,
  PersistenceConfigCore.class,
  WebServiceConfig.class,
  PublicLightingWebServiceConfig.class,
  MetricsConfig.class
})
@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsPublicLighting/config}",
    ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
  private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

  private static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
  private static final ZoneId LOCAL_TIME_ZONE = ZoneId.of(LOCAL_TIME_ZONE_IDENTIFIER);

  public static final int SECONDS_PER_MINUTE = 60;

  // TODO hier stond standaard offset oplossing moet nog worden gevonden
  private static final int TIME_ZONE_OFFSET_MINUTES =
      LOCAL_TIME_ZONE.getRules().getOffset(ZonedDateTime.now().toInstant()).getTotalSeconds()
          / SECONDS_PER_MINUTE;

  @Bean
  public PagingSettings pagingSettings() {
    return new PagingSettings(
        Integer.parseInt(
            this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE)),
        Integer.parseInt(
            this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE)));
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

  // === Time zone config ===

  @Bean
  public String localTimeZoneIdentifier() {
    return LOCAL_TIME_ZONE_IDENTIFIER;
  }

  @Bean
  public ZoneId localTimeZone() {
    return LOCAL_TIME_ZONE;
  }

  @Bean
  public Integer timeZoneOffsetMinutes() {
    return TIME_ZONE_OFFSET_MINUTES;
  }
}
