// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fake network offering {@link #openConnection(int, int)} and {@link #closeConnection(int, int)}
 * methods simulating opening and closing connections on a network segment identified by a base
 * transceiver station ID and a cell ID.
 *
 * <p>The network throws an {@link IllegalStateException} if opening a new connection causes the
 * connection count to exceed the {@link #maxConcurrentConnections maximum number of concurrent
 * connections allowed}, or if closing a connection is attempted while no connections are open.
 */
public class FakeConcurrencyRestrictedNetwork {

  private final long maxConcurrentConnections;

  private final ConcurrentMap<Integer, ConcurrentMap<Integer, Long>> connectionsPerNetworkSegment =
      new ConcurrentHashMap<>();

  public FakeConcurrencyRestrictedNetwork(final long maxConcurrentConnections) {
    if (maxConcurrentConnections < 0) {
      throw new IllegalArgumentException(
          "maxConcurrentConnections must be non-negative: " + maxConcurrentConnections);
    }
    this.maxConcurrentConnections = maxConcurrentConnections;
  }

  public synchronized void openConnection(final int baseTransceiverStationId, final int cellId) {
    final long connectionCounter =
        this.connectionsPerNetworkSegment
            .computeIfAbsent(baseTransceiverStationId, key -> new ConcurrentHashMap<>())
            .merge(cellId, 1L, (oldValue, one) -> oldValue + 1);
    if (connectionCounter > this.maxConcurrentConnections) {
      throw new IllegalStateException(
          String.format(
              "openConnection(%d, %d) called exceeding the maximum permitted number of concurrent connections: %d",
              baseTransceiverStationId, cellId, this.maxConcurrentConnections));
    }
  }

  public synchronized void closeConnection(final int baseTransceiverStationId, final int cellId) {
    final long connectionCounter =
        this.connectionsPerNetworkSegment
            .computeIfAbsent(baseTransceiverStationId, key -> new ConcurrentHashMap<>())
            .merge(cellId, -1L, (oldValue, one) -> oldValue - 1);
    if (connectionCounter < 0) {
      /*
       * Add back one to the counter, so it does not remain in an invalid state, and after opening a
       * new connection a positive value will be stored instead of zero.
       */
      this.connectionsPerNetworkSegment
          .get(baseTransceiverStationId)
          .merge(cellId, 0L, (oldValue, zero) -> oldValue + 1);
      throw new IllegalStateException(
          String.format(
              "closeConnection(%d, %d) called without open connections present",
              baseTransceiverStationId, cellId));
    }
  }
}
