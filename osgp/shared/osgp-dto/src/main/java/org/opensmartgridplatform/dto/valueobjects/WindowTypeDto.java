/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
