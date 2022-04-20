/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.mqtt.application.metrics.MqttMetricsService;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.valueobjects.MqttClientDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

class MqttClientIT {

  private static final Logger MQTT_BROKER_LOGGER = LoggerFactory.getLogger("MqttBroker");

  private final PrometheusMeterRegistry meterRegistry;

  @ClassRule
  private static final GenericContainer<?> eclipseMosquittoContainer =
      new GenericContainer<>("eclipse-mosquitto:2.0.14")
          .withExposedPorts(1883, 8883)
          .withCommand("/bin/sh", "-c", "mosquitto -c /mosquitto-no-auth.conf")
          // wait for log message, default waiting for the exposed ports uses /bin/bash
          // which is not in the container
          .waitingFor(Wait.forLogMessage("^.*mosquitto version 2.0.14 running.*$", 1));

  private static int containerMqttPort = 0;
  private static int containerMqttSslPort = 0;

  MqttClientIT() {
    this.meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    final MqttMetricsService meterService = new MqttMetricsService(this.meterRegistry);
    final MqttClientDefaults mqttClientDefaults =
        new MqttClientDefaults.Builder()
            .withClientId("test-client-id")
            .withCleanSession(false)
            .withHost("localhost")
            .withKeepAlive(60)
            .withPort(containerMqttPort)
            .withQos(MqttQos.EXACTLY_ONCE.name())
            .withTopics(new String[] {this.measurementTopic("+")})
            .build();
    this.mqttClient = new MqttClient(mqttClientDefaults, null, this.messageHandler, meterService);
  }

  @BeforeAll
  static void beforeAll() {
    eclipseMosquittoContainer.start();
    eclipseMosquittoContainer.followOutput(new Slf4jLogConsumer(MQTT_BROKER_LOGGER));
    containerMqttPort = eclipseMosquittoContainer.getMappedPort(1883);
    containerMqttSslPort = eclipseMosquittoContainer.getMappedPort(8883);
  }

  @AfterAll
  static void afterAll() {
    eclipseMosquittoContainer.stop();
  }

  private final QueuingMessageHandler messageHandler = new QueuingMessageHandler();

  final MqttClient mqttClient;

  @Test
  void messageHandlerHandlesPublishedMessages() throws Exception {
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_DISCONNECTED);
    final Mqtt3ConnAck mqtt3ConnAck = this.mqttClient.connect().get(10, TimeUnit.SECONDS);
    assertThat(this.mqttClient.isConnected())
        .as("MqttClient connect resulted in: %s", mqtt3ConnAck)
        .isTrue();
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_CONNECTED);

    try {
      final PublishedMessage message1 = this.publishedMessage("TST-01", "test-1");
      final PublishedMessage message2 = this.publishedMessage("TST-02", "test-2");
      this.publishMessages(message1, message2);

      assertThat(this.messageHandler.publishedMessages())
          .containsExactlyInAnyOrder(message1, message2);

    } finally {
      this.mqttClient.disconnect();
      // Wait one second
      Thread.sleep(1000);
      this.assertMqttGaugeStatus(MqttMetricsService.BROKER_DISCONNECTED);
    }
  }

  @Test
  void mqttClientRestoresBrokerConnection() throws Exception {
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_DISCONNECTED);
    final Mqtt3ConnAck mqtt3ConnAck = this.mqttClient.connect().get(10, TimeUnit.SECONDS);
    assertThat(this.mqttClient.isConnected())
        .as("MqttClient connect resulted in: %s", mqtt3ConnAck)
        .isTrue();
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_CONNECTED);
    final PublishedMessage message1 = this.publishedMessage("TST-01", "test-1");
    final PublishedMessage message2 = this.publishedMessage("TST-02", "test-2");
    this.publishMessages(message1, message2);
    this.theClientWillHaveReceivedThePublishedMessages(message1, message2);

    eclipseMosquittoContainer.stop();

    this.theClientWillBeWaitingForReconnect();

    // Preserve the port bindings that the client used to connect with.
    // Without this the container will bind other ports on the host.
    eclipseMosquittoContainer.setPortBindings(
        Arrays.asList(
            String.format("%d:1883", containerMqttPort),
            String.format("%d:8883", containerMqttSslPort)));
    eclipseMosquittoContainer.start();

    this.theClientWillBeConnected();
    final PublishedMessage message3 = this.publishedMessage("TST-03", "test-3");
    final PublishedMessage message4 = this.publishedMessage("TST-04", "test-4");
    this.publishMessages(message3, message4);

    this.theClientWillHaveReceivedThePublishedMessages(message1, message2, message3, message4);
  }

  private void theClientWillBeWaitingForReconnect() throws InterruptedException {
    boolean clientIsWaitingForReconnect = this.mqttClient.isWaitingForReconnect();
    final Instant timeout = Instant.now().plus(Duration.ofSeconds(10));
    while (!clientIsWaitingForReconnect && Instant.now().isBefore(timeout)) {
      TimeUnit.MILLISECONDS.sleep(100);
      clientIsWaitingForReconnect = this.mqttClient.isWaitingForReconnect();
    }

    assertThat(this.mqttClient.isWaitingForReconnect())
        .as("MQTT Broker is gone, client is waiting for reconnect")
        .isTrue();
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_RECONNECTING);
  }

  private void theClientWillBeConnected() throws InterruptedException {
    boolean clientIsConnected = this.mqttClient.isConnected();
    final Instant timeout = Instant.now().plus(Duration.ofSeconds(10));
    while (!clientIsConnected && Instant.now().isBefore(timeout)) {
      TimeUnit.MILLISECONDS.sleep(100);
      clientIsConnected = this.mqttClient.isConnected();
    }

    assertThat(this.mqttClient.isConnected())
        .as("MQTT Broker is back up, client is reconnected")
        .isTrue();
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_CONNECTED);
  }

  private void theClientWillHaveReceivedThePublishedMessages(
      final PublishedMessage... publishedMessages) throws InterruptedException {

    int numberOfPublishedMessages = this.messageHandler.publishedMessages().size();
    final int expectedNumberOfMessages = publishedMessages.length;
    final Instant timeout = Instant.now().plus(Duration.ofSeconds(10));
    while (numberOfPublishedMessages < expectedNumberOfMessages
        && Instant.now().isBefore(timeout)) {
      TimeUnit.MILLISECONDS.sleep(100);
      numberOfPublishedMessages = this.messageHandler.publishedMessages().size();
    }

    assertThat(this.messageHandler.publishedMessages())
        .containsExactlyInAnyOrder(publishedMessages);
  }

  void publishMessages(final PublishedMessage... publishedMessages) throws Exception {
    final Mqtt3AsyncClient mqtt3AsyncClient =
        Mqtt3Client.builder()
            .identifier("async-publishing-client")
            .serverHost("localhost")
            .serverPort(containerMqttPort)
            .automaticReconnectWithDefaultConfig()
            .buildAsync();

    final CountDownLatch connectLatch = new CountDownLatch(1);
    mqtt3AsyncClient.connect().whenComplete((ack, t) -> connectLatch.countDown());
    connectLatch.await();

    try {
      final CountDownLatch publishLatch = new CountDownLatch(publishedMessages.length);
      for (final PublishedMessage publishedMessage : publishedMessages) {
        mqtt3AsyncClient
            .publishWith()
            .topic(publishedMessage.topic())
            .qos(MqttQos.AT_LEAST_ONCE)
            .payload(publishedMessage.payload())
            .retain(false)
            .send()
            .whenComplete((mqtt3Publish, t) -> publishLatch.countDown());
      }
      publishLatch.await();
    } finally {
      mqtt3AsyncClient.disconnect();
    }
  }

  private PublishedMessage publishedMessage(
      final String fieldDeviceIdentification, final String payload) {

    return new PublishedMessage(this.measurementTopic(fieldDeviceIdentification), payload);
  }

  private String measurementTopic(final String fieldDeviceIdentification) {
    return String.format("%s/measurement", fieldDeviceIdentification);
  }

  private void assertMqttGaugeStatus(final int expected) {
    assertThat(this.meterRegistry.find(MqttMetricsService.CONNECTION_STATUS).gauge().value())
        .isEqualTo(expected);
  }
}
