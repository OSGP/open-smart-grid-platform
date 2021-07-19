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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MqttClientEnabledCondition.class)
public class MqttClientEnabledConfig {

  @Bean(destroyMethod = "disconnect")
  public MqttClient mqttClient(
      final MqttClientDefaults mqttClientDefaults, final MqttClientSslConfig mqttClientSslConfig) {
    final String host = mqttClientDefaults.getDefaultHost();
    final int port = mqttClientDefaults.getDefaultPort();

    final MqttClient client = new MqttClient();
    client.connect(host, port, mqttClientSslConfig);
    return client;
  }
}
