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
import java.util.List;
import java.util.UUID;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.services.MqttClientAdapter;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.services.SubscriptionService;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-mqtt.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolMqtt/config}", ignoreResourceNotFound = true)
public class MqttConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

  @Autowired private MqttDeviceRepository mqttDeviceRepository;

  @Autowired private SubscriptionService subscriptionService;

  @Autowired @Nullable private MqttClientSslConfig mqttClientSslConfig;

  private Mqtt3AsyncClient client;

  @Bean
  public MqttClientDefaults mqttClientDefaults(
      @Value("${mqtt.default.connect.to.broker:true}") final Boolean connectToBroker,
      @Value("${mqtt.default.connect.to.devices:true}") final Boolean connectToDevices,
      @Value("${mqtt.default.host:localhost}") final String defaultHost,
      @Value("${mqtt.default.port:1883}") final int defaultPort,
      @Value("${mqtt.default.qos:AT_LEAST_ONCE}") final String defaultQos,
      @Value("${mqtt.default.topics:+/measurement,+/congestion}") final String defaultTopics) {

    if (connectToBroker) {
      this.connectToBroker(defaultHost, defaultPort);
    }

    if (connectToDevices) {
      this.connectToDevices(defaultQos, defaultTopics);
    }

    return new MqttClientDefaults(defaultHost, defaultPort, defaultQos, defaultTopics);
  }

  private void connectToBroker(final String defaultHost, final int defaultPort) {
    final String id = UUID.randomUUID().toString();
    this.client =
        Mqtt3Client.builder()
            .identifier(id)
            .serverHost(defaultHost)
            .serverPort(defaultPort)
            .sslConfig(this.mqttClientSslConfig)
            .buildAsync();
    this.client.connectWith().send();
  }

  private void connectToDevices(final String defaultQos, final String defaultTopics) {

    final List<MqttDevice> devices = this.mqttDeviceRepository.findAll();
    for (final MqttDevice device : devices) {
      LOGGER.info("Connecting to MQTT device: {}", device.getDeviceIdentification());

      try {
        final MqttClientAdapter adapter =
            new MqttClientAdapter(device, null, this.mqttClientSslConfig, this.subscriptionService);
        adapter.connect();
      } catch (final Exception e) {
        LOGGER.error("Unable to connect to MQTT device: {}", device.getDeviceIdentification(), e);
      }
    }
  }
}
