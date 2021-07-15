/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
