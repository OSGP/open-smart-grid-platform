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
import org.opensmartgridplatform.adapter.protocol.mqtt.application.services.MqttClient;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MqttClientEnabledCondition.class)
public class MqttClientEnabledConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MqttClientEnabledConfig.class);
  
  @Bean(destroyMethod = "disconnect")
  public MqttClient mqttClient(
      final MqttClientDefaults mqttClientDefaults, final MqttClientSslConfig mqttClientSslConfig) {

    final MqttClient client = new MqttClient();
    LOG.info("Connecting to MQTT client with address: {}:{}", mqttClientDefaults.getDefaultHost(),mqttClientDefaults.getDefaultPort());
    client.connect(mqttClientDefaults, mqttClientSslConfig);
    return client;
  }
}
