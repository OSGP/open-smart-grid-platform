// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

/** The ActionType indexing, used by the Device schedule */
public enum ScheduleWeekday {
  WEEKEND(-2),
  WEEKDAY(-1),
  ALL(0),
  MONDAY(1),
  TUESDAY(2),
  WEDNESDAY(3),
  THURSDAY(4),
  FRIDAY(5),
  SATURDAY(6),
  SUNDAY(7);

  private int index;

  private ScheduleWeekday(final int index) {
    this.index = index;
  }

  public static ScheduleWeekday getByIndex(final int index) {
    for (final ScheduleWeekday deviceRelayType : values()) {
      if (deviceRelayType.index == index) {
        return deviceRelayType;
      }
    }

    throw new IllegalArgumentException(String.valueOf(index));
  }

  public int getIndex() {
    return this.index;
  }
}
