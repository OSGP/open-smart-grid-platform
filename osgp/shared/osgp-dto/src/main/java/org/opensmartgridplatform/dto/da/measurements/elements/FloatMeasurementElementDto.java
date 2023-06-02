//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.da.measurements.elements;

import java.util.Objects;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;

public class FloatMeasurementElementDto implements MeasurementElementDto {

  private static final long serialVersionUID = 8006310470907755173L;

  private float value;

  public FloatMeasurementElementDto(final float value) {
    this.value = value;
  }

  public float getValue() {
    return this.value;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof FloatMeasurementElementDto)) {
      return false;
    }
    final FloatMeasurementElementDto that = (FloatMeasurementElementDto) obj;
    return Float.compare(this.value, that.value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(Float.floatToIntBits(this.value));
  }

  @Override
  public String toString() {
    return "FloatMeasurementElementDto [value=" + this.value + "]";
  }
}
