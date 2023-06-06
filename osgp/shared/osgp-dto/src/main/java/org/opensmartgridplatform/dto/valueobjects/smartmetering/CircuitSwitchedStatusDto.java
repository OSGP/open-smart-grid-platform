// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum CircuitSwitchedStatusDto {
  INACTIVE(0),
  INCOMING_CALL(1),
  ACTIVE(2),
  RESERVED(3); // 3 - 255

  private final int index;

  private CircuitSwitchedStatusDto(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public static CircuitSwitchedStatusDto fromIndexValue(final int value) {
    if (value < 0 || value > 255) {
      throw new IllegalArgumentException(
          "IndexValue " + value + " not found for CircuitSwitchedStatusDto");
    }

    for (final CircuitSwitchedStatusDto status : CircuitSwitchedStatusDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    return RESERVED;
  }

  public String value() {
    return this.name();
  }

  public static CircuitSwitchedStatusDto fromValue(final String v) {
    return valueOf(v);
  }
}
