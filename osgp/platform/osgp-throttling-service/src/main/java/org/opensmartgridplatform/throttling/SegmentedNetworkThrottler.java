// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.util.Optional;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
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

    final ThrottlingConfig throttlingConfig =
        this.throttlingConfigCache.getThrottlingConfig(throttlingConfigId);

    final Optional<Integer> maxConcurrencyBtsCell =
        this.maxConcurrencyByBtsCellConfig.getMaxConcurrency(baseTransceiverStationId, cellId);
    final int maxConcurrency = maxConcurrencyBtsCell.orElse(throttlingConfig.getMaxConcurrency());
    if (maxConcurrency < 1) {
      return false;
    }

    final int maxNewConnectionRequests = throttlingConfig.getMaxNewConnectionRequests();
    final long maxNewConnectionResetTimeInMs = throttlingConfig.getMaxNewConnectionResetTimeInMs();

    return this.permitsByThrottlingConfig.requestPermit(
        throttlingConfigId,
        clientId,
        baseTransceiverStationId,
        cellId,
        requestId,
        priority,
        maxConcurrency);
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
