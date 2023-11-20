// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.entities.BtsCellConfig;
import org.opensmartgridplatform.throttling.repositories.BtsCellConfigRepository;

@ExtendWith(MockitoExtension.class)
class MaxConcurrencyByBtsCellConfigTest {
  @InjectMocks private MaxConcurrencyByBtsCellConfig btsCellConfig;
  @Mock private BtsCellConfigRepository btsCellConfigRepository;

  @Test
  void shouldFetchFromCache() {
    final int btsId = 5;
    final int cellId = 6;
    final int maxConcurrency = 9;
    this.btsCellConfig.setMaxConcurrency(btsId, cellId, maxConcurrency);

    assertThat(this.btsCellConfig.getMaxConcurrency(btsId, cellId))
        .isEqualTo(Optional.of(maxConcurrency));
  }

  @Test
  void shouldFetchFromDatabase() {
    final short cachedBtsId = 3;
    final short cachedCellId = 4;
    final int cachedMaxConcurrency = 9;
    final short nonCachedBtsId = 5;
    final short nonCachedCellId = 6;
    final int nonCachedMaxConcurrency = 11;

    this.btsCellConfig.setMaxConcurrency(cachedBtsId, cachedCellId, cachedMaxConcurrency);
    when(this.btsCellConfigRepository.findByBaseTransceiverStationIdAndCellId(
            nonCachedBtsId, nonCachedCellId))
        .thenReturn(
            Optional.of(
                new BtsCellConfig(nonCachedBtsId, nonCachedCellId, nonCachedMaxConcurrency)));

    assertThat(this.btsCellConfig.getMaxConcurrency(nonCachedBtsId, nonCachedCellId))
        .isEqualTo(Optional.of(nonCachedMaxConcurrency));
  }

  @Test
  void shouldReturnEmptyNonExistent() {
    final short btsId = 3;
    final short cellId = 4;
    when(this.btsCellConfigRepository.findByBaseTransceiverStationIdAndCellId(btsId, cellId))
        .thenReturn(Optional.empty());

    assertThat(this.btsCellConfig.getMaxConcurrency(btsId, cellId)).isEmpty();
  }
}
