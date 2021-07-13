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
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
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
      @Value("${mqtt.default.subscribe.to.topics.for.devices:true}")
          final Boolean subscribeToTopicsForDevices,
      @Value("${mqtt.default.host:localhost}") final String defaultHost,
      @Value("${mqtt.default.port:1883}") final int defaultPort,
      @Value("${mqtt.default.qos:AT_LEAST_ONCE}") final String defaultQos,
      @Value("${mqtt.default.topics:+/measurement,+/congestion}") final String defaultTopics) {

    if (connectToBroker) {
      this.connectToBroker(defaultHost, defaultPort);
    }

    if (subscribeToTopicsForDevices) {
      this.subscribeToTopicsForDevices(defaultQos, defaultTopics);
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

  private void subscribeToTopicsForDevices(final String defaultQos, final String defaultTopics) {

    final List<MqttDevice> devices = this.mqttDeviceRepository.findAll();
    for (final MqttDevice device : devices) {

      final String topics = this.getTopics(defaultTopics, device);
      final MqttQos qos = this.getQos(defaultQos, device);

      LOGGER.info(
          "Subscribing to topic(s): {} using QOS: {} for MQTT device: {}",
          topics,
          qos.toString(),
          device.getDeviceIdentification());

      try {
        final MqttClientAdapter adapter =
            new MqttClientAdapter(device, null, this.mqttClientSslConfig, this.subscriptionService);
        adapter.subscribe(topics, qos);
      } catch (final Exception e) {
        LOGGER.error(
            "Unable to subscribe to topic(s): {} using QOS: {} for MQTT device: {}",
            topics,
            qos.toString(),
            device.getDeviceIdentification(),
            e);
      }
    }
  }

  private String getTopics(final String defaultTopics, final MqttDevice device) {
    String topics;
    if (StringUtils.isEmpty(device.getTopics())) {
      topics = defaultTopics;
    } else {
      topics = device.getTopics();
    }
    return topics;
  }

  private MqttQos getQos(final String defaultQos, final MqttDevice device) {
    MqttQos qos;
    if (StringUtils.isEmpty(device.getQos())) {
      qos = MqttQos.valueOf(defaultQos);
    } else {
      qos = MqttQos.valueOf(device.getQos());
    }
    return qos;
  }
}
