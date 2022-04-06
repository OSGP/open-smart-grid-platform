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
  private final Gauge connectedGauge;

  public MqqtMetricsService(final MeterRegistry registry) {
    this.registry = registry;
    this.receivedMessagesCounter =
        this.createCounter("flexovl.nrg.pull.succeeded", "Successful NRG pull jobs");
    this.connectedGauge = this.createGauge("flexovl.nrg.pull.succeeded", "Successful NRG pull jobs");
  }

  private Counter createCounter(final String name, final String description) {
    return Counter.builder(name).description(description).register(this.registry);
  }

  private Gauge createGauge(final String name, final String description){
    return Gauge.builder(
  }
}
