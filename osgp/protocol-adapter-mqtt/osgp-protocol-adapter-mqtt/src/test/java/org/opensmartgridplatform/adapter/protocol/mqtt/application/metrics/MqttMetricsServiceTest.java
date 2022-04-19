/*
 * Copyright 2022 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.mqtt.application.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.Test;

class MqttMetricsServiceTest {

  private final MqttMetricsService service;

  private final PrometheusMeterRegistry meterRegistry;

  MqttMetricsServiceTest() {
    this.meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    this.service = new MqttMetricsService(this.meterRegistry);
  }

  @Test
  void testIncreaseReceivedMessage() {
    this.service.receivedMessage();
    assertThat(this.isMqttCounterAsExpected(1)).isTrue();
    this.service.receivedMessage();
    assertThat(this.isMqttCounterAsExpected(2)).isTrue();
    assertThat(this.isMqttCounterAsExpected(1)).isFalse();
  }

  @Test
  void testConnectedGauge() {
    this.service.connected();
    assertThat(this.isMqttGaugeStatusAsExpected(MqttMetricsService.BROKER_CONNECTED)).isTrue();
  }

  @Test
  void testDisconnectedGauge() {
    this.service.disconnected();
    assertThat(this.isMqttGaugeStatusAsExpected(MqttMetricsService.BROKER_DISCONNECTED)).isTrue();
  }

  @Test
  void testReconnectingGauge() {
    this.service.reconnecting();
    assertThat(this.isMqttGaugeStatusAsExpected(MqttMetricsService.BROKER_RECONNECTING)).isTrue();
  }

  @Test
  void testDisconnectingGauge() {
    this.service.disconnecting();
    assertThat(this.isMqttGaugeStatusAsExpected(MqttMetricsService.BROKER_DISCONNECTING)).isTrue();
  }

  private boolean isMqttCounterAsExpected(final int expected) {
    return this.meterRegistry.find(MqttMetricsService.MESSAGE_COUNTER).counter().count()
        == expected;
  }

  private boolean isMqttGaugeStatusAsExpected(final int expected) {
    return this.meterRegistry.find(MqttMetricsService.CONNECTION_STATUS).gauge().value()
        == expected;
  }
}
