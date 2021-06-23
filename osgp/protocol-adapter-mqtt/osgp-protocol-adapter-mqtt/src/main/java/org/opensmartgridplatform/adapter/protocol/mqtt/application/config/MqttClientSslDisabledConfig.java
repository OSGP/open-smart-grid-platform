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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MqttClientSslDisabledCondition.class)
public class MqttClientSslDisabledConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MqttClientSslDisabledConfig.class);

  @Bean
  public MqttClientSslConfig mqttClientSslConfig() {

    LOG.info("MQTT SSL DISABLED.");

    return null;
  }
}
