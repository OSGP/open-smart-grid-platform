//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository.PermitCountByNetworkSegment;

@ExtendWith(MockitoExtension.class)
class PermitsPerNetworkSegmentTest {

  @Mock private PermitRepository permitRepository;
  @InjectMocks private PermitsPerNetworkSegment permitsPerNetworkSegment;

  @Test
  void testInitializeEmpty() {
    final short throttlingConfigId = Short.parseShort("1");

    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(Lists.emptyList());

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
  }

  @Test
  void testInitialize() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;

    final short throttlingConfigId = Short.parseShort("1");

    final PermitCountByNetworkSegment permitCountByNetworkSegment =
        this.newPermitCountByNetworkSegment(btsId, cellId, numberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegment));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(btsId).get(cellId))
        .isEqualTo(numberOfPermits);
  }

  @Test
  void testInitializeUpdate() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    // Update number of permits in database
    final int newNumberOfPermits = 4;

    final PermitCountByNetworkSegment permitCountByNetworkSegmentUpdate =
        this.newPermitCountByNetworkSegment(btsId, cellId, newNumberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegmentUpdate));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(btsId).get(cellId))
        .isEqualTo(newNumberOfPermits);
  }

  @Test
  void testInitializeAdd() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    // Update number of permits in database
    final int newBtsId = 4;

    final PermitCountByNetworkSegment permitCountByNetworkSegmentUpdate =
        this.newPermitCountByNetworkSegment(newBtsId, cellId, numberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegmentUpdate));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().containsKey(btsId))
        .isFalse();
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(newBtsId).get(cellId))
        .isEqualTo(numberOfPermits);
  }

  @Test
  void testInitializeDelete() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    // No permits in database
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(Lists.emptyList());

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
  }

  private void preparePermits(
      final int btsId,
      final int cellId,
      final int numberOfPermits,
      final short throttlingConfigId) {
    final PermitCountByNetworkSegment permitCountByNetworkSegment =
        this.newPermitCountByNetworkSegment(btsId, cellId, numberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegment));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(btsId).get(cellId))
        .isEqualTo(numberOfPermits);
  }

  private PermitCountByNetworkSegment newPermitCountByNetworkSegment(
      final int btsId, final int cellId, final int numberOfPermits) {
    final PermitCountByNetworkSegment permitCountByNetworkSegment =
        mock(PermitCountByNetworkSegment.class);
    when(permitCountByNetworkSegment.getBaseTransceiverStationId()).thenReturn(btsId);
    when(permitCountByNetworkSegment.getCellId()).thenReturn(cellId);
    when(permitCountByNetworkSegment.getNumberOfPermits()).thenReturn(numberOfPermits);
    return permitCountByNetworkSegment;
  }
}
