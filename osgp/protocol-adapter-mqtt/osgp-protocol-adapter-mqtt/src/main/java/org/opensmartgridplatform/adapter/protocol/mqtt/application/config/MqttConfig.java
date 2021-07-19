/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-mqtt.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolMqtt/config}", ignoreResourceNotFound = true)
public class MqttConfig extends AbstractConfig {

  @Bean
  public MqttClientDefaults mqttClientDefaults(
      @Value("${mqtt.default.host:localhost}") final String defaultHost,
      @Value("${mqtt.default.port:1883}") final int defaultPort,
      @Value("${mqtt.default.qos:AT_LEAST_ONCE}") final String defaultQos,
      @Value("${mqtt.default.topics:+/measurement}") final String defaultTopics) {

    return new MqttClientDefaults(defaultHost, defaultPort, defaultQos, defaultTopics);
  }
}
