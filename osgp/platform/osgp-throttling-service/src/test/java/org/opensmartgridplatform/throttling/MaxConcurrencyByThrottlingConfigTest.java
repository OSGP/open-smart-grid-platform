/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
class MaxConcurrencyByThrottlingConfigTest {
  @InjectMocks private MaxConcurrencyByThrottlingConfig throttlingConfig;
  @Mock private ThrottlingConfigRepository throttlingConfigRepository;

  @Test
  void shouldFetchFromCache() {
    final short existingConfigId = 3;
    final int maxConcurrency = 9;
    this.throttlingConfig.setMaxConcurrency((short) 3, maxConcurrency);

    assertThat(this.throttlingConfig.getMaxConcurrency(existingConfigId)).isEqualTo(maxConcurrency);
  }

  @Test
  void shouldFetchFromDatabase() {
    final short cachedConfigId = 3;
    final int cachedMaxConcurrency = 9;
    final short nonCachedConfigId = 4;
    final int nonCachedMaxConcurrency = 11;

    this.throttlingConfig.setMaxConcurrency(cachedConfigId, cachedMaxConcurrency);
    when(this.throttlingConfigRepository.findById(nonCachedConfigId))
        .thenReturn(
            Optional.of(
                new ThrottlingConfig(
                    nonCachedConfigId, "config from db", nonCachedMaxConcurrency)));

    assertThat(this.throttlingConfig.getMaxConcurrency(nonCachedConfigId))
        .isEqualTo(nonCachedMaxConcurrency);
  }

  @Test
  void shouldFailWhenNonExistent() {
    assertThatThrownBy(() -> this.throttlingConfig.getMaxConcurrency((short) 1))
        .hasMessageContaining("unknown");
  }
}
