// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
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
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;
import org.opensmartgridplatform.throttling.service.PermitReleasedNotifier;

@ExtendWith(MockitoExtension.class)
class PermitsByThrottlingConfigTest {
  private static final boolean WAIT_FOR_HIGH_PRIO_ENABLED = true;
  private static final int MAX_WAIT_FOR_HIGH_PRIO = 1000;

  @Mock private ThrottlingConfigRepository throttlingConfigRepository;
  @Mock private PermitRepository permitRepository;
  @Mock private PermitReleasedNotifier permitReleasedNotifier;
  private PermitsByThrottlingConfig permitsByThrottlingConfig;

  @BeforeEach
  void setUp() {
    this.permitsByThrottlingConfig =
        new PermitsByThrottlingConfig(
            this.throttlingConfigRepository,
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            this.MAX_WAIT_FOR_HIGH_PRIO);
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
    final int maxNewConnectionRequests = 12;
    final long maxNewConnectionResetTimeInMs = 1000;

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                configId,
                name,
                maxConcurrency,
                maxNewConnectionRequests,
                maxNewConnectionResetTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);
    when(this.permitRepository.permitsByNetworkSegment(configId)).thenReturn(Lists.emptyList());

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1);
    assertThat(permitsPerNetworkSegmentByConfig).containsKey(configId);
  }

  @Test
  void testInitializeAdd() {
    final short configId = Integer.valueOf(1).shortValue();
    final String name = "config1";
    final int maxConcurrency = 10;
    final int maxNewConnectionRequests = 12;
    final long maxNewConnectionResetTimeInMs = 1000;

    this.prepare(
        configId, name, maxConcurrency, maxNewConnectionRequests, maxNewConnectionResetTimeInMs);

    final short newConfigId = Integer.valueOf(1).shortValue();

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                newConfigId,
                name,
                maxConcurrency,
                maxNewConnectionRequests,
                maxNewConnectionResetTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);
    when(this.permitRepository.permitsByNetworkSegment(newConfigId)).thenReturn(Lists.emptyList());

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
    final int maxNewConnectionRequests = 12;
    final long maxNewConnectionResetTimeInMs = 1000;

    this.prepare(
        configId, name, maxConcurrency, maxNewConnectionRequests, maxNewConnectionResetTimeInMs);

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                configId,
                name,
                maxConcurrency,
                maxNewConnectionRequests,
                maxNewConnectionResetTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);
    when(this.permitRepository.permitsByNetworkSegment(configId)).thenReturn(Lists.emptyList());

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
    final int maxOpenonnections = 11;
    final int maxNewConnectionRequests = 12;
    final long maxNewConnectionResetTimeInMs = 1000;

    this.prepare(
        configId, name, maxConcurrency, maxNewConnectionRequests, maxNewConnectionResetTimeInMs);
    reset(this.permitRepository);

    when(this.throttlingConfigRepository.findAll()).thenReturn(Lists.emptyList());
    verifyNoInteractions(this.permitRepository);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).isEmpty();
  }

  private void prepare(
      final short configId,
      final String name,
      final int maxConcurrency,
      final int maxNewConnectionRequests,
      final long maxNewConnectionResetTimeInMs) {
    final List<ThrottlingConfig> throttlingConfigs =
        List.of(
            new ThrottlingConfig(
                configId,
                name,
                maxConcurrency,
                maxNewConnectionRequests,
                maxNewConnectionResetTimeInMs));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);
    when(this.permitRepository.permitsByNetworkSegment(configId)).thenReturn(Lists.emptyList());

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1);
    assertThat(permitsPerNetworkSegmentByConfig).containsKey(configId);
  }
}
