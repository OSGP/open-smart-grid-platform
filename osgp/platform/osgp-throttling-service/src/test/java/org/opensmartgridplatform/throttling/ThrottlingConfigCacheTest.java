// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.opensmartgridplatform.throttling.repositories.ThrottlingConfigRepository;

@ExtendWith(MockitoExtension.class)
class ThrottlingConfigCacheTest {
  @InjectMocks private ThrottlingConfigCache throttlingConfig;
  @Mock private ThrottlingConfigRepository throttlingConfigRepository;

  @Test
  void shouldFetchFromCache() {
    final short existingConfigId = 3;
    final int maxConcurrency = 9;
    final ThrottlingConfig throttlingConfig = new ThrottlingConfig();
    throttlingConfig.setMaxConcurrency(maxConcurrency);
    this.throttlingConfig.setThrottlingConfig((short) 3, throttlingConfig);

    assertThat(this.throttlingConfig.getThrottlingConfig(existingConfigId).getMaxConcurrency())
        .isEqualTo(maxConcurrency);
  }

  @Test
  void shouldFetchFromDatabase() {
    final short cachedConfigId = 3;
    final int cachedMaxConcurrency = 9;
    final short nonCachedConfigId = 4;
    final int nonCachedMaxConcurrency = 11;
    final int nonCachedMaxNewConnections = 12;
    final long nonCachedMaxNewConnectionsResetTimeInMs = 1000;
    final long nonCachedMaxNewConnectionsWaitTimeInMs = 2000;

    final ThrottlingConfig cachedThrottlingConfig = new ThrottlingConfig();
    cachedThrottlingConfig.setMaxConcurrency(cachedMaxConcurrency);

    this.throttlingConfig.setThrottlingConfig(cachedConfigId, cachedThrottlingConfig);
    when(this.throttlingConfigRepository.findById(nonCachedConfigId))
        .thenReturn(
            Optional.of(
                new ThrottlingConfig(
                    nonCachedConfigId,
                    "config from db",
                    nonCachedMaxConcurrency,
                    nonCachedMaxNewConnections,
                    nonCachedMaxNewConnectionsResetTimeInMs,
                    nonCachedMaxNewConnectionsWaitTimeInMs)));

    assertThat(this.throttlingConfig.getThrottlingConfig(nonCachedConfigId).getMaxConcurrency())
        .isEqualTo(nonCachedMaxConcurrency);
  }

  @Test
  void shouldFailWhenNonExistent() {
    assertThatThrownBy(() -> this.throttlingConfig.getThrottlingConfig((short) 1))
        .hasMessageContaining("unknown");
  }
}
