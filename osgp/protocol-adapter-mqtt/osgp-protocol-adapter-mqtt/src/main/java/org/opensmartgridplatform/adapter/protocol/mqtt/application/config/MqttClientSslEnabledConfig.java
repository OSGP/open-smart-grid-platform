/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.opensmartgridplatform.shared.application.config.mqtt.MqttClientSslConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@Conditional(MqttClientSslEnabledCondition.class)
public class MqttClientSslEnabledConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MqttClientSslEnabledConfig.class);

  @Bean
  public MqttClientSslConfig mqttClientSslConfig(
      @Value("${mqtt.client.ssl.truststore.location}") final Resource truststoreLocation,
      @Value("${mqtt.client.ssl.truststore.password}") final String truststorePassword,
      @Value("${mqtt.client.ssl.truststore.type}") final String truststoreType) {

    LOG.info("MQTT SSL ENABLED.");

    return MqttClientSslConfigFactory.getMqttClientSslConfig(
        truststoreLocation, truststorePassword, truststoreType);
  }
}
