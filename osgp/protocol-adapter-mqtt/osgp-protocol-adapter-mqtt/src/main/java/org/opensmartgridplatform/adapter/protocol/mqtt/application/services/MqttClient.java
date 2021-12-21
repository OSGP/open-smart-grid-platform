/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

  private Mqtt3AsyncClient client;

  public static Mqtt3AsyncClient createClient(
      final String host,
      final int port,
      final MqttClientDefaults mqttClientDefaults,
      final MqttClientSslConfig mqttClientSslConfig) {

    Mqtt3ClientBuilder clientBuilder =
        Mqtt3Client.builder()
            .identifier(getClientId())
            .serverHost(host)
            .serverPort(port)
            .automaticReconnectWithDefaultConfig()
            .sslConfig(mqttClientSslConfig);

    if (StringUtils.isNotEmpty(mqttClientDefaults.getDefaultUsername())) {
      LOGGER.debug("Using username/password for MQTT connection");
      clientBuilder =
          clientBuilder
              .simpleAuth()
              .username(mqttClientDefaults.getDefaultUsername())
              .password(mqttClientDefaults.getDefaultPassword().getBytes())
              .applySimpleAuth();
    }

    return clientBuilder.buildAsync();
  }

  public Mqtt3AsyncClient connect(
      final MqttClientDefaults mqttClientDefaults, final MqttClientSslConfig mqttClientSslConfig) {
    this.client =
        createClient(
            mqttClientDefaults.getDefaultHost(),
            mqttClientDefaults.getDefaultPort(),
            mqttClientDefaults,
            mqttClientSslConfig);

    this.client
        .connectWith()
        .cleanSession(mqttClientDefaults.isDefaultCleanSession())
        .keepAlive(mqttClientDefaults.getDefaultKeepAlive())
        .send()
        .whenComplete(MqttClient::onConnect);

    return this.client;
  }

  public void disconnect() {
    if (this.client != null) {
      this.client.disconnect().whenComplete(MqttClient::onDisconnect);
      this.client = null;
    }
  }

  public Mqtt3AsyncClient getMqtt3AsyncClient() {
    return this.client;
  }

  private static void onConnect(final Mqtt3ConnAck ack, final Throwable throwable) {
    if (throwable != null) {
      LOGGER.error(
          "MQTT connection to broker not successful, error: {}", throwable.getMessage(), throwable);
    }
    if (ack != null) {
      LOGGER.info(
          "MQTT connection to broker successfully created, return code: {}", ack.getReturnCode());
    }
  }

  private static void onDisconnect(final Void nothing, final Throwable throwable) {
    if (throwable != null) {
      LOGGER.error(
          "MQTT connection might not be disconnected, error: {} {}",
          throwable.getMessage(),
          nothing,
          throwable);
    } else {
      LOGGER.info("MQTT connection disconnected.");
    }
  }

  private static String getClientId() {
    String value;
    InetAddress inetAddress;
    try {
      inetAddress = InetAddress.getLocalHost();
      value = inetAddress.getHostName() + '-' + inetAddress.getHostAddress();
      LOGGER.info("MQTT Client ID: {} (Using host information)", value);
    } catch (final UnknownHostException e) {
      value = UUID.randomUUID().toString();
      LOGGER.warn("MQTT Client ID: {} (Using random UUID)", value);
    }

    return value;
  }
}
