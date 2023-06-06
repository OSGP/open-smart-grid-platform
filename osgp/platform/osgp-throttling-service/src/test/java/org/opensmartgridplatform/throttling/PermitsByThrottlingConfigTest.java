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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;

@ExtendWith(MockitoExtension.class)
class PermitsByThrottlingConfigTest {

  @Mock private ThrottlingConfigRepository throttlingConfigRepository;
  @Mock private PermitRepository permitRepository;
  @InjectMocks private PermitsByThrottlingConfig permitsByThrottlingConfig;

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

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(new ThrottlingConfig(configId, name, maxConcurrency));
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

    this.prepare(configId, name, maxConcurrency);

    final short newConfigId = Integer.valueOf(1).shortValue();

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(new ThrottlingConfig(newConfigId, name, maxConcurrency));
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

    this.prepare(configId, name, maxConcurrency);

    final List<ThrottlingConfig> throttlingConfigs =
        List.of(new ThrottlingConfig(configId, name, maxConcurrency));
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

    this.prepare(configId, name, maxConcurrency);
    reset(this.permitRepository);

    when(this.throttlingConfigRepository.findAll()).thenReturn(Lists.emptyList());
    verifyNoInteractions(this.permitRepository);

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).isEmpty();
  }

  private void prepare(final short configId, final String name, final int maxConcurrency) {
    final List<ThrottlingConfig> throttlingConfigs =
        List.of(new ThrottlingConfig(configId, name, maxConcurrency));
    when(this.throttlingConfigRepository.findAll()).thenReturn(throttlingConfigs);
    when(this.permitRepository.permitsByNetworkSegment(configId)).thenReturn(Lists.emptyList());

    this.permitsByThrottlingConfig.initialize();

    final Map<Short, PermitsPerNetworkSegment> permitsPerNetworkSegmentByConfig =
        this.permitsByThrottlingConfig.permitsPerNetworkSegmentByConfig();
    assertThat(permitsPerNetworkSegmentByConfig).hasSize(1);
    assertThat(permitsPerNetworkSegmentByConfig).containsKey(configId);
  }
}
