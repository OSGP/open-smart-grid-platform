// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
