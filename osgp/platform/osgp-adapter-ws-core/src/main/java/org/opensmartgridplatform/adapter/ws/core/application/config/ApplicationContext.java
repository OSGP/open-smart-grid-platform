/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.config;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.ws.infra.specifications.JpaDeviceSpecifications;
import org.opensmartgridplatform.adapter.ws.infra.specifications.JpaEventSpecifications;
import org.opensmartgridplatform.adapter.ws.shared.db.application.config.WritablePersistenceConfig;
import org.opensmartgridplatform.domain.core.specifications.DeviceSpecifications;
import org.opensmartgridplatform.domain.core.specifications.EventSpecifications;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareLocation;
import org.opensmartgridplatform.logging.domain.config.ReadOnlyLoggingConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.ws.core.config.CoreWebServiceConfig;
import org.springframework.beans.factory.annotation.Qualifier;
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
      "org.opensmartgridplatform.adapter.ws.core",
      "org.opensmartgridplatform.domain.logging"
    })
@ImportResource("classpath:applicationContext.xml")
@Import({
  PersistenceConfig.class,
  WritablePersistenceConfig.class,
  ReadOnlyLoggingConfig.class,
  WebServiceConfig.class,
  CoreWebServiceConfig.class
})
@PropertySource("classpath:osgp-adapter-ws-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsCore/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  private static final String PROPERTY_NAME_DEFAULT_PROTOCOL = "default.protocol";
  private static final String PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION = "default.protocol.version";

  private static final String PROPERTY_NAME_RECENT_DEVICES_PERIOD = "recent.devices.period";

  private static final String PROPERTY_NAME_FIRMWARE_DOMAIN = "firmware.domain";
  private static final String PROPERTY_NAME_FIRMWARE_PATH = "firmware.path";
  private static final String PROPERTY_NAME_FIRMWARE_DIRECTORY = "firmware.directory";
  private static final String PROPERTY_NAME_FIRMWARE_FILESTORAGE = "firmware.filestorage";
  private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
  private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

  private static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
  private static final DateTimeZone LOCAL_TIME_ZONE =
      DateTimeZone.forID(LOCAL_TIME_ZONE_IDENTIFIER);
  private static final int TIME_ZONE_OFFSET_MINUTES =
      LOCAL_TIME_ZONE.getStandardOffset(new DateTime().getMillis())
          / DateTimeConstants.MILLIS_PER_MINUTE;

  private static final String PROPERTY_NAME_NET_MANAGEMENT_ORGANISATION =
      "net.management.organisation";

  private static final String PROPERTY_NAME_SCHEDULING_TASK_PAGE_SIZE = "scheduling.task.page.size";

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
  public Integer scheduledTaskPageSize() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_SCHEDULING_TASK_PAGE_SIZE));
  }

  @Bean
  public FirmwareLocation firmwareLocation() {
    return new FirmwareLocation(
        this.environment.getProperty(PROPERTY_NAME_FIRMWARE_DOMAIN),
        this.environment.getProperty(PROPERTY_NAME_FIRMWARE_PATH));
  }

  @Bean
  public String firmwareDirectory() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_FIRMWARE_DIRECTORY);
  }

  @Bean
  public boolean firmwareFileStorage() {
    return Boolean.parseBoolean(
        this.environment.getRequiredProperty(PROPERTY_NAME_FIRMWARE_FILESTORAGE));
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
  public String localTimeZoneIdentifier() {
    return LOCAL_TIME_ZONE_IDENTIFIER;
  }

  @Bean
  public DateTimeZone localTimeZone() {
    return LOCAL_TIME_ZONE;
  }

  @Bean
  public int timeZoneOffsetMinutes() {
    return TIME_ZONE_OFFSET_MINUTES;
  }

  @Bean
  @Qualifier("wsCoreDeviceManagementNetManagementOrganisation")
  public String netMangementOrganisation() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_NET_MANAGEMENT_ORGANISATION);
  }
}
