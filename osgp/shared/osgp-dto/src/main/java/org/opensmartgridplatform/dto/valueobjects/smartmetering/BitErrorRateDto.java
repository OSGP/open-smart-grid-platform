/**
 * Copyright 2020 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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

  public static BitErrorRateDto fromValue(final int value) {
    for (final BitErrorRateDto status : BitErrorRateDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    return null;
  }
}
