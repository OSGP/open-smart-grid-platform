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

@Component
public class MqqtMetricsService {
  private final MeterRegistry registry;
  private final Counter receivedMessagesCounter;
  private final Gauge disconnectedGauge;
  private Object[] disconnectedList;
  private static final Object[] CONNECTED = {};
  private static final Object[] RECONNECTING = {1};
  private static final Object[] DISCONNECTED = {0, 1};

  public MqqtMetricsService(final MeterRegistry registry) {
    this.registry = registry;
    this.disconnectedList = DISCONNECTED;
    this.receivedMessagesCounter =
        this.createCounter("flexovl.nrg.pull.succeeded", "Successful NRG pull jobs");
    this.disconnectedGauge =
        this.createGauge(
            "flexovl.nrg.pull.succeeded", "Successful NRG pull jobs", this.disconnectedList, "3");
  }

  public void receivedMessage() {
    this.receivedMessagesCounter.increment();
  }

  public void disconnected() {
    if (!this.disconnectedList.equals(DISCONNECTED)) {
      this.disconnectedList = DISCONNECTED;
    }
  }

  public void reconnecting() {
    if (!this.disconnectedList.equals(RECONNECTING)) {
      this.disconnectedList = RECONNECTING;
    }
  }

  public void connected() {
    if (!this.disconnectedList.equals(CONNECTED)) {
      this.disconnectedList = CONNECTED;
      this.disconnectedGauge
    }
  }

  private Gauge createGauge(
      final String name, final String description, final Object[] list, final String baseUnit) {
    return Gauge.builder(name, () -> list.length)
        .description(description)
        .baseUnit(baseUnit)
        .register(this.registry);
  }

  private Counter createCounter(final String name, final String description) {
    return Counter.builder(name).description(description).register(this.registry);
  }
}
