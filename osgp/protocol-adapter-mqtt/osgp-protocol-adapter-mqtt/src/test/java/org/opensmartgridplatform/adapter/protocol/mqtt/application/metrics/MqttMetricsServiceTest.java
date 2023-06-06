// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.mqtt.application.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.metrics.MetricsNameService;

@ExtendWith(MockitoExtension.class)
class MqttMetricsServiceTest {

  @InjectMocks private MqttMetricsService service;

  @Spy
  private final PrometheusMeterRegistry meterRegistry =
      new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

  @Spy private MetricsNameService metricsNameService;

  @Mock private Mqtt3AsyncClient mqttClient;

  @BeforeEach
  void setUp() {
    this.service.monitorConnectionStatus(this.mqttClient);
  }

  @Test
  void testIncreaseReceivedMessage() {
    this.service.receivedMessage();
    this.assertMqttCounter(1);
    this.service.receivedMessage();
    this.assertMqttCounter(2);
  }

  @Test
  void testConnectedGauge() {
    when(this.mqttClient.getState()).thenReturn(MqttClientState.CONNECTED);
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_CONNECTED);
  }

  @Test
  void testDisconnectedGauge() {
    when(this.mqttClient.getState()).thenReturn(MqttClientState.DISCONNECTED);
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_DISCONNECTED);
  }

  @Test
  void testReconnectingGauge() {
    when(this.mqttClient.getState()).thenReturn(MqttClientState.DISCONNECTED_RECONNECT);
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_RECONNECTING);
  }

  @Test
  void testDisconnectingGauge() {
    when(this.mqttClient.getState()).thenReturn(MqttClientState.DISCONNECTED_RECONNECT);
    this.assertMqttGaugeStatus(MqttMetricsService.BROKER_RECONNECTING);
  }

  private void assertMqttCounter(final int expected) {
    assertThat(this.meterRegistry.find(MqttMetricsService.MESSAGE_COUNTER).counter().count())
        .isEqualTo(expected);
  }

  private void assertMqttGaugeStatus(final int expected) {
    assertThat(this.meterRegistry.find(MqttMetricsService.CONNECTION_STATUS).gauge().value())
        .isEqualTo(expected);
  }
}
