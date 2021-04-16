/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
