/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.FirmwareLocation;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** An application context Java configuration class. */
@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.adapter.protocol.oslp.elster",
      "org.opensmartgridplatform.core.db.api"
    })
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-oslp-elster.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterProtocolOslpElster/config}",
    ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  private static final String PROPERTY_NAME_FIRMWARE_DOMAIN = "firmware.domain";
  private static final String PROPERTY_NAME_FIRMWARE_PATH = "firmware.path";
  private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
  private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

  private static final String PROPERTY_NAME_LOCAL_TIME_ZONE_IDENTIFIER = "local.time.zone";

  private static final String PROPERTY_NAME_DEVICE_PENDINGSETSCHEDULEREQUEST_EXPIRES_IN_MINUTES =
      "device.pendingsetschedulerequest.expires_in_minutes";

  public ApplicationContext() {
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    final JmxConfig jmxConfig = key -> null;
    Metrics.addRegistry(new JmxMeterRegistry(jmxConfig, Clock.SYSTEM));
  }

  @Bean
  public String successfulMessagesMetric() {
    return "oslp.successful.messages";
  }

  @Bean
  public String failedMessagesMetric() {
    return "oslp.failed.messages";
  }

  @Bean(name = "oslpPagingSettings")
  public PagingSettings pagingSettings() {
    return new PagingSettings(
        Integer.parseInt(
            this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE)),
        Integer.parseInt(
            this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE)));
  }

  @Bean
  public FirmwareLocation firmwareLocation() {
    return new FirmwareLocation(
        this.environment.getProperty(PROPERTY_NAME_FIRMWARE_DOMAIN),
        this.environment.getProperty(PROPERTY_NAME_FIRMWARE_PATH));
  }

  @Bean
  public Integer pendingSetScheduleRequestExpiresInMinutes() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_DEVICE_PENDINGSETSCHEDULEREQUEST_EXPIRES_IN_MINUTES));
  }

  // === Time zone config ===

  @Bean
  public String localTimeZoneIdentifier() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_LOCAL_TIME_ZONE_IDENTIFIER);
  }

  @Bean
  public DateTimeZone localTimeZone() {
    return DateTimeZone.forID(this.localTimeZoneIdentifier());
  }

  @Bean
  public Integer timeZoneOffsetMinutes() {
    return this.localTimeZone().getStandardOffset(new DateTime().getMillis())
        / DateTimeConstants.MILLIS_PER_MINUTE;
  }
}
