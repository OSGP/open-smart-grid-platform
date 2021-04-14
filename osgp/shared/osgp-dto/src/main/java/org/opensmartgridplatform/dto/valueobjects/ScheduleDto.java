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
import java.util.List;

public class ScheduleDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 6516779611853805357L;

  private final Short astronomicalSunriseOffset;
  private final Short astronomicalSunsetOffset;

  private final List<ScheduleEntryDto> scheduleList;

  public ScheduleDto(final List<ScheduleEntryDto> scheduleList) {
    this(scheduleList, null, null);
  }

  public ScheduleDto(
      final List<ScheduleEntryDto> scheduleList,
      final Short astronomicalSunriseOffset,
      final Short astronomicalSunsetOffset) {
    this.scheduleList = scheduleList;
    this.astronomicalSunriseOffset = astronomicalSunriseOffset;
    this.astronomicalSunsetOffset = astronomicalSunsetOffset;
  }

  public Short getAstronomicalSunriseOffset() {
    return this.astronomicalSunriseOffset;
  }

  public Short getAstronomicalSunsetOffset() {
    return this.astronomicalSunsetOffset;
  }

  public List<ScheduleEntryDto> getScheduleList() {
    return this.scheduleList;
  }
}
