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
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.datatypes.MqttClientIdentifier;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAckReturnCode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

  private final MqttClientDefaults mqttClientDefaults;
  private final MessageHandler messageHandler;
  private final String clientIdentifier;
  private final Mqtt3AsyncClient client;

  public MqttClient(
      final MqttClientDefaults mqttClientDefaults,
      final MqttClientSslConfig mqttClientSslConfig,
      final MessageHandler messageHandler) {

    this.mqttClientDefaults = mqttClientDefaults;
    this.messageHandler = messageHandler;
    this.clientIdentifier = getClientId(mqttClientDefaults);
    this.client = this.createClient(mqttClientDefaults, mqttClientSslConfig, this.clientIdentifier);
  }

  private static String getClientId(final MqttClientDefaults mqttClientDefaults) {
    String value = mqttClientDefaults.getDefaultClientId();
    if (StringUtils.isBlank(value)) {
      try {
        final InetAddress inetAddress = InetAddress.getLocalHost();
        value = inetAddress.getHostName() + '-' + inetAddress.getHostAddress();
        LOGGER.info("MQTT Client ID: {} (Using host information)", value);
      } catch (final UnknownHostException e) {
        value = UUID.randomUUID().toString();
        LOGGER.warn("MQTT Client ID: {} (Using random UUID)", value);
      }
    } else {
      LOGGER.info("MQTT Client ID: {} (Using configuration property)", value);
    }
    return value;
  }

  private Mqtt3AsyncClient createClient(
      final MqttClientDefaults mqttClientDefaults,
      final MqttClientSslConfig mqttClientSslConfig,
      final String clientIdentifier) {

    Mqtt3ClientBuilder clientBuilder =
        Mqtt3Client.builder()
            .identifier(clientIdentifier)
            .serverHost(mqttClientDefaults.getDefaultHost())
            .serverPort(mqttClientDefaults.getDefaultPort())
            .sslConfig(mqttClientSslConfig)
            .automaticReconnectWithDefaultConfig()
            .addConnectedListener(
                context ->
                    LOGGER.info(
                        "{} client {} connected to broker at {}:{}",
                        context.getClientConfig().getMqttVersion(),
                        context
                            .getClientConfig()
                            .getClientIdentifier()
                            .map(MqttClientIdentifier::toString)
                            .orElse(clientIdentifier),
                        context.getClientConfig().getServerHost(),
                        context.getClientConfig().getServerPort()))
            .addDisconnectedListener(
                context ->
                    LOGGER.info(
                        "{} client {} disconnected from broker at {}:{}",
                        context.getClientConfig().getMqttVersion(),
                        context
                            .getClientConfig()
                            .getClientIdentifier()
                            .map(MqttClientIdentifier::toString)
                            .orElse(clientIdentifier),
                        context.getClientConfig().getServerHost(),
                        context.getClientConfig().getServerPort()));

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

  public CompletableFuture<Mqtt3ConnAck> connect() {
    return this.client
        .connectWith()
        .cleanSession(this.mqttClientDefaults.isDefaultCleanSession())
        .keepAlive(this.mqttClientDefaults.getDefaultKeepAlive())
        .send()
        .whenComplete(this::onConnect);
  }

  private void onConnect(final Mqtt3ConnAck ack, final Throwable throwable) {
    if (this.isConnectSuccess(ack, throwable)) {
      this.onConnectSuccess();
    } else {
      this.onConnectFailure(ack, throwable);
    }
  }

  private boolean isConnectSuccess(final Mqtt3ConnAck ack, final Throwable throwable) {
    return (throwable == null
        && (ack != null && ack.getReturnCode() == Mqtt3ConnAckReturnCode.SUCCESS));
  }

  private void onConnectSuccess() {
    LOGGER.info("MQTT Client {} connected", this.clientIdentifier);
    this.subscribe();
  }

  private void onConnectFailure(final Mqtt3ConnAck ack, final Throwable throwable) {
    LOGGER.warn("MQTT Client {} connect failed, ack: {}", this.clientIdentifier, ack, throwable);
  }

  public void disconnect() {
    if (!this.isDisconnected()) {
      this.client.disconnect().whenComplete(this::onDisconnect);
    }
  }

  private void onDisconnect(final Void nothing, final Throwable throwable) {
    if (throwable != null) {
      LOGGER.error(
          "MQTT Client {} connection might not be disconnected, state: {}",
          this.clientIdentifier,
          this.client.getState(),
          throwable);
    } else {
      LOGGER.info("MQTT Client {} connection disconnected.", this.clientIdentifier);
    }
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

  public void subscribe() {
    this.subscribe(this.messageHandler);
  }

  public void subscribe(final MessageHandler messageHandler) {
    if (this.isDisconnected()) {
      LOGGER.warn("Skipping subscribe, MQTT Client {} is disconnected", this.clientIdentifier);
      return;
    } else if (this.isWaitingForReconnect()) {
      LOGGER.warn(
          "Skipping subscribe, MQTT Client {} is waiting for reconnect", this.clientIdentifier);
      return;
    }
    final MqttQos qos = MqttQos.valueOf(this.mqttClientDefaults.getDefaultQos());
    Arrays.stream(this.mqttClientDefaults.getDefaultTopics())
        .forEach(
            topic -> {
              this.unsubscribe(topic);
              this.subscribe(topic, qos, messageHandler);
            });
  }

  public void subscribe(final String topic, final MqttQos qos) {
    this.subscribe(topic, qos, this.messageHandler);
  }

  public void subscribe(
      final String topic, final MqttQos qos, final MessageHandler messageHandler) {

    LOGGER.info(
        "Subscribing client: {} to topic: {} with QoS: {}", this.clientIdentifier, topic, qos);
    this.client
        .subscribeWith()
        .topicFilter(topic)
        .qos(qos)
        .callback(mqttPublish -> this.published(mqttPublish, messageHandler))
        .send()
        .whenComplete((subAck, throwable) -> this.onSubscribe(topic, qos, subAck, throwable));
  }

  private void published(final Mqtt3Publish publish, final MessageHandler messageHandler) {
    messageHandler.handlePublishedMessage(
        publish.getTopic().toString(), publish.getPayloadAsBytes());
  }

  private void onSubscribe(
      final String topic, final MqttQos qos, final Mqtt3SubAck ack, final Throwable throwable) {

    if (this.isSubscribeSuccess(ack, throwable)) {
      this.onSubscribeSuccess(topic, qos, ack);
    } else {
      this.onSubscribeFailure(topic, qos, ack, throwable);
    }
  }

  private boolean isSubscribeSuccess(final Mqtt3SubAck ack, final Throwable throwable) {
    return (throwable == null
        && (ack != null
            && ack.getReturnCodes().stream().noneMatch(rc -> rc == Mqtt3SubAckReturnCode.FAILURE)));
  }

  private void onSubscribeSuccess(final String topic, final MqttQos qos, final Mqtt3SubAck ack) {
    LOGGER.info(
        "MQTT Client {} subscribed to topic: {}, qos: {},  ack: {}",
        this.clientIdentifier,
        topic,
        qos,
        ack);
  }

  private void onSubscribeFailure(
      final String topic, final MqttQos qos, final Mqtt3SubAck ack, final Throwable throwable) {

    LOGGER.warn(
        "MQTT Client {} subscription failed for topic: {}, qos: {}, ack: {}",
        this.clientIdentifier,
        topic,
        qos,
        ack,
        throwable);
  }

  public void unsubscribe(final String topic) {
    LOGGER.info("Unsubscribing client: {} from topic: {}", this.clientIdentifier, topic);
    this.client
        .unsubscribeWith()
        .topicFilter(topic)
        .send()
        .whenComplete((v, throwable) -> this.onUnsubscribe(topic, throwable));
  }

  public void onUnsubscribe(final String topic, final Throwable throwable) {
    if (throwable == null) {
      LOGGER.info("MQTT Client {} unsubscribed from topic: {}", this.clientIdentifier, topic);
    } else {
      LOGGER.warn(
          "MQTT Client {} unsubscribe failed for topic: {}", this.clientIdentifier, throwable);
    }
  }
}
