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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class MqttMetricsService {

  public static final int BROKER_DISCONNECTED = 0;
  public static final int BROKER_DISCONNECTING = 1;
  public static final int BROKER_RECONNECTING = 2;
  public static final int BROKER_CONNECTED = 3;

  private final MeterRegistry registry;
  private final Counter receivedMessagesCounter;
  private final AtomicInteger connectionStatus = new AtomicInteger(0);

  public static final String MESSAGE_COUNTER = "mqtt.metrics.counter.received.messages";
  public static final String CONNECTION_STATUS = "mqtt.metrics.gauge.connection.status";

  /**
   * @param registry
   */
  public MqttMetricsService(final MeterRegistry registry) {
    this.registry = registry;
    this.receivedMessagesCounter =
        this.createCounter(
            MESSAGE_COUNTER, "Counter with the amount of received messages by the MQTT layer");
    this.createGauge(
        CONNECTION_STATUS,
        this.connectionStatus::get,
        "Gauge to show if the MQTT layer is connected to the provided MQTT broker");
  }

  public void receivedMessage() {
    this.receivedMessagesCounter.increment();
  }

  public void connected() {
    this.connectionStatus.set(BROKER_CONNECTED);
  }

  public void reconnecting() {
    this.connectionStatus.set(BROKER_RECONNECTING);
  }

  public void disconnecting() {
    this.connectionStatus.set(BROKER_DISCONNECTING);
  }

  public void disconnected() {
    this.connectionStatus.set(BROKER_DISCONNECTED);
  }

  private void createGauge(
      final String name, final Supplier<Number> numberSupplier, final String description) {

    Gauge.builder(name, numberSupplier).description(description).register(this.registry);
  }

  private Counter createCounter(final String name, final String description) {
    return Counter.builder(name).description(description).register(this.registry);
  }
}
