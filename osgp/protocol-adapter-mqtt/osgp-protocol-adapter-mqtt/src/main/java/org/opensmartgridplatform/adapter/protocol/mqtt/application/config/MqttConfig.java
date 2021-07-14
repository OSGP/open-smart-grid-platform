/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import java.util.UUID;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({MqttClientSslEnabledConfig.class, MqttClientSslDisabledConfig.class})
@PropertySource("classpath:osgp-adapter-protocol-mqtt.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolMqtt/config}", ignoreResourceNotFound = true)
public class MqttConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

  @Bean
  public MqttClientDefaults mqttClientDefaults(
      @Value("${mqtt.default.host:localhost}") final String defaultHost,
      @Value("${mqtt.default.port:1883}") final int defaultPort,
      @Value("${mqtt.default.qos:AT_LEAST_ONCE}") final String defaultQos,
      @Value("${mqtt.default.topics:+/measurement}") final String defaultTopics) {

    return new MqttClientDefaults(defaultHost, defaultPort, defaultQos, defaultTopics);
  }

  @Bean
  public Mqtt3AsyncClient mqttClient(
      @Value("${mqtt.default.connect.to.broker:false}") final boolean connectToBroker,
      final MqttClientDefaults mqttClientDefaults,
      final MqttClientSslConfig mqttClientSslConfig) {
    final String host = mqttClientDefaults.getDefaultHost();
    final int port = mqttClientDefaults.getDefaultPort();

    if (connectToBroker) {
      return this.connectToBroker(host, port, mqttClientSslConfig);
    } else {
      return null;
    }
  }

  private Mqtt3AsyncClient connectToBroker(
      final String host, final int port, final MqttClientSslConfig mqttClientSslConfig) {
    final String id = UUID.randomUUID().toString();
    final Mqtt3AsyncClient client =
        Mqtt3Client.builder()
            .identifier(id)
            .serverHost(host)
            .serverPort(port)
            .sslConfig(mqttClientSslConfig)
            .buildAsync();
    client
        .connectWith()
        .send()
        .whenComplete((ack, throwable) -> this.onConnect(this, ack, throwable));

    return client;
  }

  void onConnect(final MqttConfig mqttConfig, final Mqtt3ConnAck ack, final Throwable throwable) {
    if (throwable != null) {
      LOGGER.error(
          "MQTT connection to broker not successful, error: {}", throwable.getMessage(), throwable);
    }
    if (ack != null) {
      LOGGER.info(
          "MQTT connection to broker successfully created, return code: {}", ack.getReturnCode());
    }
  }
}
