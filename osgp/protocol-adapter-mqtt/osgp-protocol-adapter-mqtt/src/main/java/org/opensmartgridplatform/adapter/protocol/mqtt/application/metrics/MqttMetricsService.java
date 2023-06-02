//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.mqtt.application.metrics;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.function.Supplier;
import org.opensmartgridplatform.shared.metrics.MetricsNameService;
import org.springframework.stereotype.Component;

@Component
public class MqttMetricsService {

  public static final int BROKER_DISCONNECTED = 0;
  public static final int BROKER_RECONNECTING = 1;
  public static final int BROKER_CONNECTED = 2;

  private final MeterRegistry registry;
  private final MetricsNameService metricsNameService;
  private final Counter receivedMessagesCounter;

  public static final String MESSAGE_COUNTER = "mqtt.metrics.counter.received.messages";
  public static final String CONNECTION_STATUS = "mqtt.metrics.gauge.connection.status";
  private Mqtt3AsyncClient client;

  /**
   * Prometheus' metrics service to count the amount of messages received by the MQTT client and to
   * get a connection status from the MQTT client.
   *
   * @param registry the Prometheus meter registry
   * @param metricsNameService Service for creating application-specific metric names
   */
  public MqttMetricsService(
      final MeterRegistry registry, final MetricsNameService metricsNameService) {
    this.registry = registry;
    this.metricsNameService = metricsNameService;
    this.receivedMessagesCounter = this.createCounter();
    this.createGauge(this::getMappedState);
  }

  public void receivedMessage() {
    this.receivedMessagesCounter.increment();
  }

  public void monitorConnectionStatus(final Mqtt3AsyncClient client) {
    this.client = client;
  }

  private int getMappedState() {
    if (this.client != null) {
      switch (this.client.getState()) {
        case CONNECTED:
          return BROKER_CONNECTED;
        case DISCONNECTED:
          return BROKER_DISCONNECTED;
        default:
          return BROKER_RECONNECTING;
      }
    } else {
      return BROKER_DISCONNECTED;
    }
  }

  private void createGauge(final Supplier<Number> numberSupplier) {
    Gauge.builder(this.metricsNameService.createName(CONNECTION_STATUS), numberSupplier)
        .description("Gauge to show if the MQTT layer is connected to the provided MQTT broker")
        .register(this.registry);
  }

  private Counter createCounter() {
    return Counter.builder(this.metricsNameService.createName(MESSAGE_COUNTER))
        .description("Counter with the amount of received messages by the MQTT layer")
        .register(this.registry);
  }
}
