// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.enums;

import lombok.Getter;

public enum ClockBase {
  NOT_DEFINED(0, "not defined"),
  INTERNAL_CRYSTAL(1, "internal crystal"),
  MAINS_50_HZ(2, "mains frequency 50 Hz"),
  MAINS_60_HZ(3, "mains frequency 60 Hz"),
  GPS(4, "GPS"),
  RADIO_CONTROLLED(5, "radio controlled"),
  UNKNOWN_CLOCK_BASE(255, "unknown clock base");

  private final int value;

  @Getter private final String description;

  ClockBase(final int value, final String description) {
    this.value = value;
    this.description = description;
  }

  public static ClockBase getByValue(final int value) {
    for (final ClockBase method : ClockBase.values()) {
      if (method.value == value) {
        return method;
      }
    }

    return UNKNOWN_CLOCK_BASE;
  }
}
