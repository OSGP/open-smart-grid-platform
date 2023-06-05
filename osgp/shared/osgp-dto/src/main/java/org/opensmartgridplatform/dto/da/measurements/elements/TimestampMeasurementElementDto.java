// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
