//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.EnumSet;
import java.util.Set;

// suppress warning for duplicate string at reserved (line 21,22,23). The way it
// is currently done is according to the blue book 12th edition at paragraph 4.1.6.1
@SuppressWarnings("squid:S1192")
public enum ClockStatusBit {
  INVALID_VALUE("invalid value"),
  DOUBTFUL_VALUE("doubtful value"),
  DIFFERENT_CLOCK_BASE("different clock base"),
  INVALID_CLOCK_STATUS("invalid clock status"),
  RESERVED_1("reserved"),
  RESERVED_2("reserved"),
  RESERVED_3("reserved"),
  DAYLIGHT_SAVING_ACTIVE("daylight saving active");

  private final String description;

  ClockStatusBit(final String description) {
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

  public static Set<ClockStatusBit> forClockStatus(final byte clockStatus) {
    return forClockStatus(clockStatus & 0xFF);
  }

  // SonarQube complains about the null return. SonarQube wants to see a
  // return of an empty set. The problem is that the behavior of the code
  // differs depending on whether the return value is null or an empty set.
  // Trying to correct this would result in other behavior changes, making a
  // simple fix not possible. It is still an issue though, meaning it
  // shouldn't be suppressed.
  public static Set<ClockStatusBit> forClockStatus(final int clockStatus) {
    if (ClockStatus.STATUS_NOT_SPECIFIED == clockStatus) {
      return null;
    }
    if (clockStatus < 0 || clockStatus > 0xFF) {
      throw new IllegalArgumentException("clockStatus not in [0..255]");
    }
    final Set<ClockStatusBit> statusBits = EnumSet.noneOf(ClockStatusBit.class);
    for (final ClockStatusBit statusBit : values()) {
      if (statusBit.isSet(clockStatus)) {
        statusBits.add(statusBit);
      }
    }
    return statusBits;
  }

  public static int getStatus(final Set<ClockStatusBit> statusBits) {
    if (statusBits == null) {
      return ClockStatus.STATUS_NOT_SPECIFIED;
    }
    int status = 0;
    for (final ClockStatusBit statusBit : statusBits) {
      status |= (1 << statusBit.ordinal());
    }
    return status;
  }
}
