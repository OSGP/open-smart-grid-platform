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

import org.opensmartgridplatform.shared.config.PrometheusDisabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(PrometheusDisabledCondition.class)
public class DefaultMetricsService implements MetricsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMetricsService.class);

  public DefaultMetricsService() {
    LOGGER.info("Default metrics service created");
  }

  @Override
  public void increaseCounter(final String name) {
    // Do nothing
  }

  @Override
  public void increaseCounter(final String name, final String description) {
    // Do nothing
  }

  @Override
  public void longTime(final Runnable runnable, final String name) {
    runnable.run();
  }

  @Override
  public void longTime(final Runnable runnable, final String name, final String description) {
    runnable.run();
  }
}
