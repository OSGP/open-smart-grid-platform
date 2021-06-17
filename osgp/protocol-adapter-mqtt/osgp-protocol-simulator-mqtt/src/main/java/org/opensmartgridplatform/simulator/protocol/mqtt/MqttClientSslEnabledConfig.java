/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import org.opensmartgridplatform.shared.application.config.mqtt.MqttClientSslConfigFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConditionalOnProperty(name = "mqtt.simulator.ssl.enabled", havingValue = "true")
public class MqttClientSslEnabledConfig {

  @Bean
  public MqttClientSslConfig mqttClientSslConfig(
      @Value("${mqtt.simulator.client.ssl.truststore.location}") final Resource truststoreLocation,
      @Value("${mqtt.simulator.client.ssl.truststore.password}") final String truststorePassword,
      @Value("${mqtt.simulator.client.ssl.truststore.type}") final String truststoreType) {

    return MqttClientSslConfigFactory.getMqttClientSslConfig(
        truststoreLocation, truststorePassword, truststoreType);
  }
}
