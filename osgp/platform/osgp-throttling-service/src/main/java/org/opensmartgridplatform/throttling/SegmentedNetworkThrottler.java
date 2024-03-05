// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.util.Optional;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.springframework.stereotype.Component;

@Component
public class SegmentedNetworkThrottler {

  private final ThrottlingConfigCache throttlingConfigCache;
  private final MaxConcurrencyByBtsCellConfig maxConcurrencyByBtsCellConfig;
  private final PermitsByThrottlingConfig permitsByThrottlingConfig;

  public SegmentedNetworkThrottler(
      final ThrottlingConfigCache throttlingConfigCache,
      final MaxConcurrencyByBtsCellConfig maxConcurrencyByBtsCellConfig,
      final PermitsByThrottlingConfig permitsByThrottlingConfig) {

    this.throttlingConfigCache = throttlingConfigCache;
    this.maxConcurrencyByBtsCellConfig = maxConcurrencyByBtsCellConfig;
    this.permitsByThrottlingConfig = permitsByThrottlingConfig;
  }

  public boolean requestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final int priority) {

    final ThrottlingSettings throttlingSettings =
        this.getThrottlingSettings(throttlingConfigId, baseTransceiverStationId, cellId);

    return this.permitsByThrottlingConfig.requestPermit(
        throttlingConfigId,
        clientId,
        baseTransceiverStationId,
        cellId,
        requestId,
        priority,
        throttlingSettings);
  }

  private ThrottlingSettings getThrottlingSettings(
      final short throttlingConfigId, final int baseTransceiverStationId, final int cellId) {
    final ThrottlingConfig throttlingConfig =
        this.throttlingConfigCache.getThrottlingConfig(throttlingConfigId);

    final Optional<Integer> maxConcurrencyBtsCell =
        this.maxConcurrencyByBtsCellConfig.getMaxConcurrency(baseTransceiverStationId, cellId);
    final int maxConcurrency = maxConcurrencyBtsCell.orElse(throttlingConfig.getMaxConcurrency());

    final int maxNewConnections = throttlingConfig.getMaxNewConnections();
    final long maxNewConnectionsResetTimeInMs =
        throttlingConfig.getMaxNewConnectionsResetTimeInMs();
    final long maxNewConnectionsWaitTimeInMs = throttlingConfig.getMaxNewConnectionsWaitTimeInMs();

    return new ThrottlingSettings() {
      @Override
      public int getMaxConcurrency() {
        return maxConcurrency;
      }

      @Override
      public int getMaxNewConnections() {
        return maxNewConnections;
      }

      @Override
      public long getMaxNewConnectionsResetTimeInMs() {
        return maxNewConnectionsResetTimeInMs;
      }

      @Override
      public long getMaxNewConnectionsWaitTimeInMs() {
        return maxNewConnectionsWaitTimeInMs;
      }
    };
  }

  public boolean releasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    return this.permitsByThrottlingConfig.releasePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);
  }

  public boolean discardPermit(final int clientId, final int requestId) {
    return this.permitsByThrottlingConfig.discardPermit(clientId, requestId);
  }
}
