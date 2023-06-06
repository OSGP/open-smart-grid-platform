// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetClockConfigurationRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -4876678355925159193L;

  protected final short timeZoneOffset;

  protected final CosemDateTimeDto daylightSavingsBegin;

  protected final CosemDateTimeDto daylightSavingsEnd;

  protected final boolean daylightSavingsEnabled;

  public SetClockConfigurationRequestDto(
      final short timeZoneOffset,
      final CosemDateTimeDto daylightSavingsBegin,
      final CosemDateTimeDto daylightSavingsEnd,
      final boolean daylightSavingsEnabled) {
    this.timeZoneOffset = timeZoneOffset;
    this.daylightSavingsBegin = daylightSavingsBegin;
    this.daylightSavingsEnd = daylightSavingsEnd;
    this.daylightSavingsEnabled = daylightSavingsEnabled;
  }

  public short getTimeZoneOffset() {
    return this.timeZoneOffset;
  }

  public CosemDateTimeDto getDaylightSavingsBegin() {
    return this.daylightSavingsBegin;
  }

  public CosemDateTimeDto getDaylightSavingsEnd() {
    return this.daylightSavingsEnd;
  }

  public boolean isDaylightSavingsEnabled() {
    return this.daylightSavingsEnabled;
  }
}
