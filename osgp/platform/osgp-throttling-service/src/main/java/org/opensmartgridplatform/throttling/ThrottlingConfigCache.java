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
public class ThrottlingConfigCache {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingConfigCache.class);

  private final ConcurrentMap<Short, ThrottlingConfig> throttlingConfigByConfigId =
      new ConcurrentHashMap<>();

  private final ThrottlingConfigRepository throttlingConfigRepository;

  public ThrottlingConfigCache(final ThrottlingConfigRepository throttlingConfigRepository) {

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
                this.throttlingConfigByConfigId.putIfAbsent(
                    throttlingConfig.getId(), throttlingConfig));
    stopWatch.stop();
    LOGGER.info("Init took {}ms", stopWatch.getLastTaskTimeMillis());
  }

  /**
   * Clears all cached throttling configuration and initializes the cached information from the
   * database.
   */
  void reset() {
    this.throttlingConfigByConfigId.clear();
    this.initialize();
  }

  public Map<Short, ThrottlingConfig> throttlingConfigByConfigId() {
    return new TreeMap<>(this.throttlingConfigByConfigId);
  }

  public void setThrottlingConfig(
      final short throttlingConfigId, final ThrottlingConfig throttlingConfig) {
    this.throttlingConfigByConfigId.put(throttlingConfigId, throttlingConfig);
  }

  public ThrottlingConfig getThrottlingConfig(final short throttlingConfigId) {
    final ThrottlingConfig throttlingConfig =
        this.throttlingConfigByConfigId.get(throttlingConfigId);
    if (throttlingConfig != null) {
      return throttlingConfig;
    }
    return this.updateThrottlingConfigFromDatabase(throttlingConfigId);
  }

  private ThrottlingConfig updateThrottlingConfigFromDatabase(final short throttlingConfigId) {
    final ThrottlingConfig throttlingConfig =
        this.throttlingConfigRepository
            .findById(throttlingConfigId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "throttlingConfigId is unknown: " + throttlingConfigId));
    this.throttlingConfigByConfigId.putIfAbsent(throttlingConfigId, throttlingConfig);
    return this.throttlingConfigByConfigId.get(throttlingConfigId);
  }
}
