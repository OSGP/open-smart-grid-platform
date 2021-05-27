/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import java.util.TimeZone;
import ma.glasnost.orika.MapperFacade;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.Iec60870Mapper;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-iec60870.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterProtocolIec60870/config}",
    ignoreResourceNotFound = true)
public class Iec60870Config extends AbstractConfig {

  private static final String PROPERTY_NAME_CONNECTION_RESPONSE_TIMEOUT =
      "connection.response.timeout";
  private static final String PROPERTY_NAME_IEC60870_TIMEOUT_CONNECT = "iec60870.timeout.connect";

  private static final String PROPERTY_NAME_IEC60870_TIME_ZONE = "iec60870.time.zone";

  /**
   * The amount of time, in milliseconds, the library will wait for a response after sending a
   * request.
   */
  @Bean
  public int responseTimeout() {
    return Integer.parseInt(
        this.environment.getProperty(PROPERTY_NAME_CONNECTION_RESPONSE_TIMEOUT));
  }

  @Bean
  public int connectionTimeout() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_TIMEOUT_CONNECT));
  }

  @Bean
  public MapperFacade iec60870Mapper() {
    final String timeZone = this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_TIME_ZONE);
    return new Iec60870Mapper(TimeZone.getTimeZone(timeZone));
  }
}
