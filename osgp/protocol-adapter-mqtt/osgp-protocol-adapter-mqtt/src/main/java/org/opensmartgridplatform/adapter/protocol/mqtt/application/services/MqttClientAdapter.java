/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

public class MqttClientAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(MqttClientAdapter.class);

  private final MqttDevice device;
  private final MessageMetadata messageMetadata;
  private final MqttClientDefaults mqttClientDefaults;
  private final MqttClientSslConfig mqttClientSslConfig;
  private final MqttClientEventHandler mqttClientEventHandler;
  @NotNull private final Mqtt3AsyncClient client;

  public MqttClientAdapter(
      @NotNull final MqttDevice device,
      @NotNull final MessageMetadata messageMetadata,
      @Nullable final Mqtt3AsyncClient mqtt3AsyncClient,
      @NotNull final MqttClientDefaults mqttClientDefaults,
      @Nullable final MqttClientSslConfig mqttClientSslConfig,
      @NotNull final MqttClientEventHandler mqttClientEventHandler) {

    this.device = device;
    this.messageMetadata = messageMetadata;
    this.mqttClientDefaults = mqttClientDefaults;
    this.mqttClientSslConfig = mqttClientSslConfig;
    this.mqttClientEventHandler = mqttClientEventHandler;

    if (mqtt3AsyncClient != null) {
      this.client = mqtt3AsyncClient;
    } else {
      this.client =
          MqttClient.createClient(
              this.device.getHost(),
              this.device.getPort(),
              this.mqttClientDefaults,
              this.mqttClientSslConfig);
    }
  }

  public void connect() {
    if (this.client.getState() == MqttClientState.DISCONNECTED) {
      this.client
          .connectWith()
          .cleanSession(this.mqttClientDefaults.isDefaultCleanSession())
          .keepAlive(this.mqttClientDefaults.getDefaultKeepAlive())
          .send()
          .whenComplete(
              (ack, throwable) -> this.mqttClientEventHandler.onConnect(this, ack, throwable));
    }
  }

  public void disconnect() {
    this.client
        .disconnect()
        .whenComplete((v, t) -> this.mqttClientEventHandler.onDisconnect(this, t));
  }

  public void subscribe() {
    if (this.isConnected()) {
      final MqttQos qos = this.getQosOrDefault(this.device);
      Arrays.stream(this.device.getTopicsArray())
          .forEach(
              topic -> {
                this.unsubscribe(topic);
                this.subscribe(topic, qos);
              });
    } else {
      LOG.warn("Skipping subscribe, client is not connected");
    }
  }

  private void subscribe(final String topic, final MqttQos qos) {
    LOG.info("Subscribing client: {} to topic: {} with QOS: {}", this.client, topic, qos);
    this.client
        .subscribeWith()
        .topicFilter(topic)
        .qos(qos)
        .callback(this::publishPayload)
        .send()
        .whenComplete(
            (subAck, throwable) ->
                this.mqttClientEventHandler.onSubscribe(this, topic, qos, subAck, throwable));
  }

  public void unsubscribe(final String topic) {
    LOG.info("Unsubscribing client: {} from topic: {}", this.client, topic);
    this.client
        .unsubscribeWith()
        .topicFilter(topic)
        .send()
        .whenComplete(
            (v, throwable) -> this.mqttClientEventHandler.onUnsubscribe(this, topic, throwable));
  }

  private void publishPayload(final Mqtt3Publish publish) {
    publish
        .getPayload()
        .ifPresent(
            byteBuffer -> this.mqttClientEventHandler.onReceive(this, publish.getPayloadAsBytes()));
  }

  public boolean isConnected() {
    return this.client.getState() == MqttClientState.CONNECTED;
  }

  public boolean isDisconnected() {
    return this.client.getState() == MqttClientState.DISCONNECTED;
  }

  public boolean isWaitingForReconnect() {
    return this.client.getState() == MqttClientState.DISCONNECTED_RECONNECT
        || this.client.getState() == MqttClientState.CONNECTING_RECONNECT;
  }

  public MqttDevice getDevice() {
    return this.device;
  }

  public MessageMetadata getMessageMetadata() {
    return this.messageMetadata;
  }

  private MqttQos getQosOrDefault(final MqttDevice device) {
    MqttQos mqttQos;
    try {
      mqttQos = MqttQos.valueOf(device.getQos());
    } catch (final IllegalArgumentException | NullPointerException e) {
      LOG.warn("Illegal or missing QoS value {}, using default", device.getQos(), e);
      device.setQos(this.mqttClientDefaults.getDefaultQos());
      mqttQos = MqttQos.valueOf(device.getQos());
    }
    return mqttQos;
  }
}
