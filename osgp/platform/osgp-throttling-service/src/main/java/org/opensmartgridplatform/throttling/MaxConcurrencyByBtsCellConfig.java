// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.opensmartgridplatform.throttling.entities.BtsCellConfig;
import org.opensmartgridplatform.throttling.repositories.BtsCellConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class MaxConcurrencyByBtsCellConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(MaxConcurrencyByBtsCellConfig.class);

  private static final int NO_VALUE_FOUND = -1;

  private final ConcurrentMap<String, Integer> maxConcurrencyByBtsCell = new ConcurrentHashMap<>();

  private final BtsCellConfigRepository btsCellConfigRepository;

  public MaxConcurrencyByBtsCellConfig(final BtsCellConfigRepository btsCellConfigRepository) {

    this.btsCellConfigRepository = btsCellConfigRepository;
  }

  @PostConstruct
  private void initialize() {
    final StopWatch stopWatch = new StopWatch(this.getClass().getSimpleName());
    stopWatch.start();
    this.btsCellConfigRepository
        .findAll()
        .forEach(
            btsCellConfig ->
                this.maxConcurrencyByBtsCell.putIfAbsent(
                    this.getBtsCellKey(
                        btsCellConfig.getBaseTransceiverStationId(), btsCellConfig.getCellId()),
                    btsCellConfig.getMaxConcurrency()));
    stopWatch.stop();
    LOGGER.info("Init took {}ms", stopWatch.getLastTaskTimeMillis());
  }

  /**
   * Clears all cached throttling configuration and initializes the cached information from the
   * database.
   */
  void reset() {
    this.maxConcurrencyByBtsCell.clear();
    this.initialize();
  }

  public void setMaxConcurrency(
      final int baseTransceiverStationId, final int cellId, final int maxConcurrency) {
    this.maxConcurrencyByBtsCell.put(
        this.getBtsCellKey(baseTransceiverStationId, cellId), maxConcurrency);
  }

  public Optional<Integer> getMaxConcurrency(final int baseTransceiverStationId, final int cellId) {
    final String btsCellKey = this.getBtsCellKey(baseTransceiverStationId, cellId);
    Integer maxConcurrency = this.maxConcurrencyByBtsCell.get(btsCellKey);
    if (maxConcurrency == null) {
      maxConcurrency = this.updateMaxConcurrencyFromDatabase(baseTransceiverStationId, cellId);
    }
    return Optional.ofNullable(maxConcurrency != NO_VALUE_FOUND ? maxConcurrency : null);
  }

  private int updateMaxConcurrencyFromDatabase(
      final int baseTransceiverStationId, final int cellId) {
    final int maxConcurrency =
        this.btsCellConfigRepository
            .findByBaseTransceiverStationIdAndCellId(baseTransceiverStationId, cellId)
            .map(BtsCellConfig::getMaxConcurrency)
            .orElse(NO_VALUE_FOUND);
    final String btsCellKey = this.getBtsCellKey(baseTransceiverStationId, cellId);
    this.maxConcurrencyByBtsCell.putIfAbsent(btsCellKey, maxConcurrency);
    return this.maxConcurrencyByBtsCell.get(btsCellKey);
  }

  private String getBtsCellKey(final int baseTransceiverStationId, final int cellId) {
    return String.format("%s-%s", baseTransceiverStationId, cellId);
  }
}
