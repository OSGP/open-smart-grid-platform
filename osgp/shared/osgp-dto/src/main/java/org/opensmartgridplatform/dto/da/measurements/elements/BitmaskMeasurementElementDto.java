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

public class BitmaskMeasurementElementDto implements MeasurementElementDto {

  private static final long serialVersionUID = 8653430832984993508L;

  private final byte value;

  public BitmaskMeasurementElementDto(final byte value) {
    this.value = value;
  }

  public byte getValue() {
    return this.value;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof BitmaskMeasurementElementDto)) {
      return false;
    }
    final BitmaskMeasurementElementDto that = (BitmaskMeasurementElementDto) obj;
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return "BitmaskMeasurementElementDto [value=" + this.value + "]";
  }
}
