/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import java.io.Serializable;
import java.util.Objects;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;

public class FloatMeasurementElement implements MeasurementElement, Serializable {

  private static final long serialVersionUID = 1L;

  private Float value;

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
