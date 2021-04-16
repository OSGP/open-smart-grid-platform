/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;

public class Schedule implements Serializable {

  /** Serial version UID. */
  private static final long serialVersionUID = 280691205768966372L;

  private final Short astronomicalSunriseOffset;
  private final Short astronomicalSunsetOffset;

  @Valid private final List<ScheduleEntry> scheduleEntries;

  public Schedule(final List<ScheduleEntry> scheduleEntries) {
    this(scheduleEntries, null, null);
  }

  public Schedule(
      final List<ScheduleEntry> scheduleEntries,
      final Short astronomicalSunriseOffset,
      final Short astronomicalSunsetOffset) {
    this.astronomicalSunriseOffset = astronomicalSunriseOffset;
    this.astronomicalSunsetOffset = astronomicalSunsetOffset;
    this.scheduleEntries = scheduleEntries;
  }

  public Short getAstronomicalSunriseOffset() {
    return this.astronomicalSunriseOffset;
  }

  public Short getAstronomicalSunsetOffset() {
    return this.astronomicalSunsetOffset;
  }

  public List<ScheduleEntry> getScheduleEntries() {
    return this.scheduleEntries;
  }
}
