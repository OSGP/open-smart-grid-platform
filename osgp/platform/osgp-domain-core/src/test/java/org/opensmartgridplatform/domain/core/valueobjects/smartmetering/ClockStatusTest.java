// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class ClockStatusTest {
  private static final ClockStatus CLOCK_STATUS_NOT_SPECIFIED =
      new ClockStatus((byte) ClockStatus.STATUS_NOT_SPECIFIED);

  @Test
  void testGetStatusWithEmptyStatusBits() {
    final ClockStatus clockStatus = new ClockStatus(EnumSet.noneOf(ClockStatusBit.class));

    final int result = clockStatus.getStatus();

    assertThat(result).isZero();
  }

  @Test
  void testGetStatusWithStatusNotSpecified() {
    final int result = CLOCK_STATUS_NOT_SPECIFIED.getStatus();

    assertThat(result).isEqualTo(ClockStatus.STATUS_NOT_SPECIFIED);
  }

  @Test
  void testHasStatusBitWithStatusNotSpeficied() {
    final boolean result = CLOCK_STATUS_NOT_SPECIFIED.hasStatusBits();

    assertThat(result).isFalse();
  }
}
