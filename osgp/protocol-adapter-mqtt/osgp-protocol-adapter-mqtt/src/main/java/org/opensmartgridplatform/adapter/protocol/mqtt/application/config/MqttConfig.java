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

  @Value("${mqtt.default.clean.session:true}")
  private boolean defaultCleanSession;

  @Value("${mqtt.default.client.id:#{null}}")
  private String defaultClientId;

  @Value("${mqtt.default.host:localhost}")
  private String defaultHost;

  @Value("${mqtt.default.keep.alive:60}")
  private int defaultKeepAlive;

  @Value("${mqtt.default.password:#{null}}")
  private String defaultPassword;

  @Value("${mqtt.default.port:1883}")
  private int defaultPort;

  @Value("${mqtt.default.qos:AT_LEAST_ONCE}")
  private String defaultQos;

  @Value("${mqtt.default.topics:+/measurement}")
  private String[] defaultTopics;

  @Value("${mqtt.default.username:#{null}}")
  private String defaultUsername;

  @Bean
  public MqttClientDefaults mqttClientDefaults() {

    return new MqttClientDefaults.Builder()
        .withCleanSession(this.defaultCleanSession)
        .withClientId(this.defaultClientId)
        .withHost(this.defaultHost)
        .withKeepAlive(this.defaultKeepAlive)
        .withPassword(this.defaultPassword)
        .withPort(this.defaultPort)
        .withQos(this.defaultQos)
        .withTopics(this.defaultTopics)
        .withUsername(this.defaultUsername)
        .build();
  }
}
