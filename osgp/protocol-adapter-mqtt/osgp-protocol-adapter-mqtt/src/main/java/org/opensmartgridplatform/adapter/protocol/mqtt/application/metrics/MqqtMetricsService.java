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
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Component
public class MqqtMetricsService {
  private final MeterRegistry registry;
  private final Counter receivedMessagesCounter;
  private final AtomicInteger disconnected = new AtomicInteger(1);
  private final AtomicInteger connected = new AtomicInteger(0);
  private final AtomicInteger reconnecting = new AtomicInteger(0);

  public MqqtMetricsService(final MeterRegistry registry) {
    this.registry = registry;
    this.receivedMessagesCounter =
        this.createCounter("mqtt.metrics.counter.received.messages", "Counter with the amount of received messages by the MQTT layer");
    this.createGauge(
            "mqtt.metrics.gauge.disconnected", this.disconnected::get,"Gauge to show if the MQTT layer is disconnected to the provided MQTT broker");
    this.createGauge(
            "mqtt.metrics.gauge.connected", this.connected::get,"Gauge to show if the MQTT layer is connected to the provided MQTT broker");
    this.createGauge(
            "mqtt.metrics.gauge.reconnecting", this.reconnecting::get,"Gauge to show if the MQTT layer is reconnecting to the provided MQTT broker");
  }

  public void receivedMessage() {
    this.receivedMessagesCounter.increment();
  }

  public void disconnected() {
    disconnected.set(1);
    connected.set(0);
    reconnecting.set(0);
  }

  public void reconnecting() {
    disconnected.set(0);
    connected.set(0);
    reconnecting.set(1);
  }

  public void connected() {
    disconnected.set(0);
    connected.set(1);
    reconnecting.set(0);
  }

  private void createGauge(
          final String name, final Supplier<Number> numberSupplier, final String description) {

    Gauge.builder(name, numberSupplier).description(description).register(this.registry);
  }

  private Counter createCounter(final String name, final String description) {
    return Counter.builder(name).description(description).register(this.registry);
  }
}
