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
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

  private Mqtt3AsyncClient client;

  public Mqtt3AsyncClient connect(
      final String host, final int port, final MqttClientSslConfig mqttClientSslConfig) {
    final String id = UUID.randomUUID().toString();
    this.client =
        Mqtt3Client.builder()
            .identifier(id)
            .serverHost(host)
            .serverPort(port)
            .sslConfig(mqttClientSslConfig)
            .buildAsync();
    this.client.connectWith().send().whenComplete(MqttClient::onConnect);

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
}
