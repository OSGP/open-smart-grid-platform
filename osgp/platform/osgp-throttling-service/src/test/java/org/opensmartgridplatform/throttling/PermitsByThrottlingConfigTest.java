// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;
import org.opensmartgridplatform.throttling.services.PermitService;
import org.opensmartgridplatform.throttling.services.RateLimitService;

@ExtendWith(MockitoExtension.class)
class PermitsByThrottlingConfigTest {
  private static final boolean WAIT_FOR_HIGH_PRIO_ENABLED = true;
  private static final int MAX_WAIT_FOR_HIGH_PRIO = 1000;

  @Mock private ThrottlingConfigRepository throttlingConfigRepository;
  @Mock private PermitService permitService;
  @Mock private RateLimitService rateLimitService;

  private PermitsByThrottlingConfig permitsByThrottlingConfig;

  @BeforeEach
  void setUp() {
    this.permitsByThrottlingConfig =
        new PermitsByThrottlingConfig(
            this.throttlingConfigRepository,
            this.permitService,
            this.rateLimitService,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            MAX_WAIT_FOR_HIGH_PRIO);
  }

  @Test
  void testInitializeEmpty() {
    when(this.throttlingConfigRepository.findAll()).thenReturn(Lists.emptyList());

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).isEmpty();
  }

  @Test
  void testInitialize() {
    final short configId = Integer.valueOf(1).shortValue();
    final String name = "config1";
    final int maxConcurrency = 10;
    final int maxNewConnections = 12;
    final long maxNewConnectionsResetTimeInMs = 1000;
    final long maxNewConnectionsWaitTimeInMs = 1000;

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                configId,
                name,
                maxConcurrency,
                maxNewConnections,
                maxNewConnectionsResetTimeInMs,
                maxNewConnectionsWaitTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1).containsKey(configId);
  }

  @Test
  void testInitializeAdd() {
    final short configId = Integer.valueOf(1).shortValue();
    final String name = "config1";
    final int maxConcurrency = 10;
    final int maxNewConnections = 12;
    final long maxNewConnectionsResetTimeInMs = 1000;
    final long maxNewConnectionsWaitTimeInMs = 1000;

    this.prepare(
        configId,
        name,
        maxConcurrency,
        maxNewConnections,
        maxNewConnectionsResetTimeInMs,
        maxNewConnectionsWaitTimeInMs);

    final short newConfigId = Integer.valueOf(1).shortValue();

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                newConfigId,
                name,
                maxConcurrency,
                maxNewConnections,
                maxNewConnectionsResetTimeInMs,
                maxNewConnectionsWaitTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1);
    assertThat(permitsPerNetworkSegmentByConfig).containsKey(newConfigId);
  }

  @Test
  void testInitializeUpdate() {
    final short configId = Integer.valueOf(1).shortValue();
    final String name = "config1";
    final int maxConcurrency = 10;
    final int maxNewConnections = 12;
    final long maxNewConnectionsResetTimeInMs = 1000;
    final long maxNewConnectionsWaitTimeInMs = 1000;

    this.prepare(
        configId,
        name,
        maxConcurrency,
        maxNewConnections,
        maxNewConnectionsResetTimeInMs,
        maxNewConnectionsWaitTimeInMs);

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                configId,
                name,
                maxConcurrency,
                maxNewConnections,
                maxNewConnectionsResetTimeInMs,
                maxNewConnectionsWaitTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1);
    assertThat(permitsPerNetworkSegmentByConfig).containsKey(configId);
  }

  @Test
  void testInitializeDelete() {
    final short configId = Integer.valueOf(1).shortValue();
    final String name = "config1";
    final int maxConcurrency = 10;
    final int maxNewConnections = 12;
    final long maxNewConnectionsResetTimeInMs = 1000;
    final long maxNewConnectionsWaitTimeInMs = 1000;

    this.prepare(
        configId,
        name,
        maxConcurrency,
        maxNewConnections,
        maxNewConnectionsResetTimeInMs,
        maxNewConnectionsWaitTimeInMs);

    when(this.throttlingConfigRepository.findAll()).thenReturn(Lists.emptyList());
    verifyNoInteractions(this.permitService);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).isEmpty();
  }

  private void prepare(
      final short configId,
      final String name,
      final int maxConcurrency,
      final int maxNewConnections,
      final long maxNewConnectionsResetTimeInMs,
      final long maxNewConnectionsWaitTimeInMs) {
    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                configId,
                name,
                maxConcurrency,
                maxNewConnections,
                maxNewConnectionsResetTimeInMs,
                maxNewConnectionsWaitTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1);
    assertThat(permitsPerNetworkSegmentByConfig).containsKey(configId);
  }
}
