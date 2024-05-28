// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.enums;

import lombok.Getter;

public enum DayOfWeek {
  MONDAY(1, "Monday"),
  TUESDAY(2, "Tuesday"),
  WEDNESDAY(3, "Wednesday"),
  THURSDAY(4, "Thursday"),
  FRIDAY(5, "Friday"),
  SATURDAY(6, "Saturday"),
  SUNDAY(7, "Sunday"),
  NOT_SPECIFIED(255, "Day of week not specified");

  private final int value;

  @Getter private final String description;

  DayOfWeek(final int value, final String description) {
    this.value = value;
    this.description = description;
  }

  public static DayOfWeek getByValue(final int value) {
    for (final DayOfWeek method : DayOfWeek.values()) {
      if (method.value == value) {
        return method;
      }
    }

    return null;
  }
}
