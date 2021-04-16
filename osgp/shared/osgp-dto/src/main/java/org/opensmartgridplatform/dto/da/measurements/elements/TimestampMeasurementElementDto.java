/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.da.measurements.elements;

import java.util.Objects;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;

public class TimestampMeasurementElementDto implements MeasurementElementDto {

  private static final long serialVersionUID = -2553027612374582194L;

  private long value;

  public TimestampMeasurementElementDto(final long value) {
    this.value = value;
  }

  public long getValue() {
    return this.value;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof TimestampMeasurementElementDto)) {
      return false;
    }

    final TimestampMeasurementElementDto that = (TimestampMeasurementElementDto) obj;
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return "TimestampMeasurementElementDto [value=" + this.value + "]";
  }
}
