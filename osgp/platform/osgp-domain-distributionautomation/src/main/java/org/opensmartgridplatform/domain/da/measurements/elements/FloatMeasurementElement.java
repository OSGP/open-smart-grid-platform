//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.da.measurements.elements;

import java.util.Objects;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;

public class FloatMeasurementElement implements MeasurementElement {

  private static final long serialVersionUID = 1L;

  private final Float value;

  public FloatMeasurementElement(final Float value) {
    this.value = value;
  }

  public Float getValue() {
    return this.value;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof FloatMeasurementElement)) {
      return false;
    }
    final FloatMeasurementElement that = (FloatMeasurementElement) obj;
    return Float.compare(this.value, that.value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(Float.floatToIntBits(this.value));
  }

  @Override
  public String toString() {
    return "Float: " + this.value;
  }
}
