/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.EnumSet;
import java.util.Set;

public enum ClockStatusBitDto {
  INVALID_VALUE("invalid value"),
  DOUBTFUL_VALUE("doubtful value"),
  DIFFERENT_CLOCK_BASE("different clock base"),
  INVALID_CLOCK_STATUS("invalid clock status"),
  RESERVED_1("reserved"),
  RESERVED_2("reserved"),
  RESERVED_3("reserved"),
  DAYLIGHT_SAVING_ACTIVE("daylight saving active");

  private final String description;

  ClockStatusBitDto(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean isSet(final int clockStatus) {
    final int mask = 1 << this.ordinal();
    return mask == (mask & clockStatus);
  }

  @Override
  public String toString() {
    return this.description;
  }

  public static Set<ClockStatusBitDto> forClockStatus(final byte clockStatus) {
    return forClockStatus(clockStatus & 0xFF);
  }

  public static Set<ClockStatusBitDto> forClockStatus(final int clockStatus) {
    if (ClockStatusDto.STATUS_NOT_SPECIFIED == clockStatus) {
      return null;
    }
    if (clockStatus < 0 || clockStatus > 0xFF) {
      throw new IllegalArgumentException("clockStatus not in [0..255]");
    }
    final Set<ClockStatusBitDto> statusBits = EnumSet.noneOf(ClockStatusBitDto.class);
    for (final ClockStatusBitDto statusBit : values()) {
      if (statusBit.isSet(clockStatus)) {
        statusBits.add(statusBit);
      }
    }
    return statusBits;
  }

  public static int getStatus(final Set<ClockStatusBitDto> statusBits) {
    if (statusBits == null) {
      return ClockStatusDto.STATUS_NOT_SPECIFIED;
    }
    int status = 0;
    for (final ClockStatusBitDto statusBit : statusBits) {
      status |= (1 << statusBit.ordinal());
    }
    return status;
  }
}
