//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class WindowTypeDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6875513618481725063L;

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
