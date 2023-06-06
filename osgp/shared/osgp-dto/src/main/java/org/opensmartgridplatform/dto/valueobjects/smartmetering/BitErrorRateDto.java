// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum BitErrorRateDto {
  RXQUAL_0(0),
  RXQUAL_1(1),
  RXQUAL_2(2),
  RXQUAL_3(3),
  RXQUAL_4(4),
  RXQUAL_5(5),
  RXQUAL_6(6),
  RXQUAL_7(7),
  NOT_KNOWN_OR_NOT_DETECTABLE(99);

  private final int index;

  private BitErrorRateDto(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public static BitErrorRateDto fromIndexValue(final int value) {
    for (final BitErrorRateDto status : BitErrorRateDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("IndexValue " + value + " not found for BitErrorRateDto");
  }

  public String value() {
    return this.name();
  }

  public static BitErrorRateDto fromValue(final String v) {
    return valueOf(v);
  }
}
