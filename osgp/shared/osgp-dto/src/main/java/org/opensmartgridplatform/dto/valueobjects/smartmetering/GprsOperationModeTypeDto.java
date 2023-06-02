//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Arrays;
import java.util.Optional;

public enum GprsOperationModeTypeDto {
  ALWAYS_ON(1),
  TRIGGERED(2);

  private final int number;

  GprsOperationModeTypeDto(final int number) {
    this.number = number;
  }

  public int getNumber() {
    return this.number;
  }

  public static Optional<GprsOperationModeTypeDto> forNumber(final int number) {
    return Arrays.stream(values()).filter(v -> v.number == number).findAny();
  }
}
