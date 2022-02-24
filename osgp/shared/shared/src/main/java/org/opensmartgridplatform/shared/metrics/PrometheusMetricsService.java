/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import org.opensmartgridplatform.shared.config.PrometheusEnabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(PrometheusEnabledCondition.class)
public class PrometheusMetricsService implements MetricsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusMetricsService.class);

  private final MeterRegistry meterRegistry;

  public PrometheusMetricsService(final MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    LOGGER.info("Prometheus metrics service created");
  }

  @Override
  public void increaseCounter(final String name) {
    Counter.builder(name).register(this.meterRegistry).increment();
  }

  @Override
  public void increaseCounter(final String name, final String description) {
    Counter.builder(name).description(description).register(this.meterRegistry).increment();
  }

  @Override
  public void longTime(final Runnable runnable, final String name) {
    LongTaskTimer.builder(name).register(this.meterRegistry).record(runnable);
  }

  @Override
  public void longTime(final Runnable runnable, final String name, final String description) {
    LongTaskTimer.builder(name)
        .description(description)
        .register(this.meterRegistry)
        .record(runnable);
  }
}
