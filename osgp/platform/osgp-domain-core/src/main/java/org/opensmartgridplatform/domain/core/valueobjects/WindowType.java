//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class WindowType implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8216467608629392658L;

  private long minutesBefore;
  private long minutesAfter;

  public long getMinutesBefore() {
    return this.minutesBefore;
  }

  public void setMinutesBefore(final long value) {
    this.minutesBefore = value;
  }

  public long getMinutesAfter() {
    return this.minutesAfter;
  }

  public void setMinutesAfter(final long value) {
    this.minutesAfter = value;
  }
}
