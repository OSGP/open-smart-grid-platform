/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.da.measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MeasurementDto implements Serializable {

  private static final long serialVersionUID = -7800836859182549458L;

  private final List<MeasurementElementDto> measurementElements;

  public MeasurementDto(final List<MeasurementElementDto> measurementElements) {
    this.measurementElements = new ArrayList<>(measurementElements);
  }

  public List<MeasurementElementDto> getMeasurementElements() {
    return Collections.unmodifiableList(this.measurementElements);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof MeasurementDto)) {
      return false;
    }
    final MeasurementDto that = (MeasurementDto) obj;
    return Objects.equals(this.measurementElements, that.measurementElements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.measurementElements);
  }

  @Override
  public String toString() {
    return "MeasurementDto [measurementElements=" + this.measurementElements + "]";
  }
}
