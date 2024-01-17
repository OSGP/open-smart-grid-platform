// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class MaxConcurrencyByThrottlingConfig {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MaxConcurrencyByThrottlingConfig.class);

  private final ConcurrentMap<Short, Integer> maxConcurrencyByConfigId = new ConcurrentHashMap<>();

  private final ThrottlingConfigRepository throttlingConfigRepository;

  public MaxConcurrencyByThrottlingConfig(
      final ThrottlingConfigRepository throttlingConfigRepository) {

    this.throttlingConfigRepository = throttlingConfigRepository;
  }

  @PostConstruct
  private void initialize() {
    final StopWatch stopWatch = new StopWatch(this.getClass().getSimpleName());
    stopWatch.start();
    this.throttlingConfigRepository
        .findAll()
        .forEach(
            throttlingConfig ->
                this.maxConcurrencyByConfigId.putIfAbsent(
                    throttlingConfig.getId(), throttlingConfig.getMaxConcurrency()));
    stopWatch.stop();
    LOGGER.info("Init took {}ms", stopWatch.getLastTaskTimeMillis());
  }

  /**
   * Clears all cached throttling configuration and initializes the cached information from the
   * database.
   */
  void reset() {
    this.maxConcurrencyByConfigId.clear();
    this.initialize();
  }

  public Map<Short, Integer> maxConcurrencyByConfigId() {
    return new TreeMap<>(this.maxConcurrencyByConfigId);
  }

  public void setMaxConcurrency(final short throttlingConfigId, final int maxConcurrency) {
    this.maxConcurrencyByConfigId.put(throttlingConfigId, maxConcurrency);
  }

  public int getMaxConcurrency(final short throttlingConfigId) {
    final int maxConcurrency = this.maxConcurrencyByConfigId.getOrDefault(throttlingConfigId, -1);
    if (maxConcurrency > -1) {
      return maxConcurrency;
    }
    return this.updateMaxConcurrencyFromDatabase(throttlingConfigId);
  }

  private int updateMaxConcurrencyFromDatabase(final short throttlingConfigId) {
    final int maxConcurrency =
        this.throttlingConfigRepository
            .findById(throttlingConfigId)
            .map(ThrottlingConfig::getMaxConcurrency)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "throttlingConfigId is unknown: " + throttlingConfigId));
    this.maxConcurrencyByConfigId.putIfAbsent(throttlingConfigId, maxConcurrency);
    return this.maxConcurrencyByConfigId.get(throttlingConfigId);
  }
}
