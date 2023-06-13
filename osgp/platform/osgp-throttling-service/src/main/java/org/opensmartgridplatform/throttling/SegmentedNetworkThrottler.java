// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import org.springframework.stereotype.Component;

@Component
public class SegmentedNetworkThrottler {

  private final MaxConcurrencyByThrottlingConfig maxConcurrencyByThrottlingConfig;
  private final PermitsByThrottlingConfig permitsByThrottlingConfig;

  public SegmentedNetworkThrottler(
      final MaxConcurrencyByThrottlingConfig maxConcurrencyByThrottlingConfig,
      final PermitsByThrottlingConfig permitsByThrottlingConfig) {

    this.maxConcurrencyByThrottlingConfig = maxConcurrencyByThrottlingConfig;
    this.permitsByThrottlingConfig = permitsByThrottlingConfig;
  }

  public boolean requestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    final int maxConcurrency =
        this.maxConcurrencyByThrottlingConfig.getMaxConcurrency(throttlingConfigId);
    if (maxConcurrency < 1) {
      return false;
    }

    return this.permitsByThrottlingConfig.requestPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, maxConcurrency);
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
