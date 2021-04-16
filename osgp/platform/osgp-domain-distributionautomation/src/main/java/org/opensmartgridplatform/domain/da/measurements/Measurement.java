/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Measurement implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<MeasurementElement> measurementElements = new ArrayList<>();

  public Measurement(final List<MeasurementElement> measurementElements) {
    this.measurementElements = new ArrayList<>(measurementElements);
  }

  public static Measurement of(final MeasurementElement... elements) {
    return new Measurement(Arrays.asList(elements));
  }

  public List<MeasurementElement> getMeasurementElements() {
    return new ArrayList<>(this.measurementElements);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Measurement)) {
      return false;
    }

    final Measurement that = (Measurement) obj;
    return Objects.equals(this.measurementElements, that.measurementElements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.measurementElements);
  }

  @Override
  public String toString() {
    return "Measurement [measurementElements=" + this.measurementElements + "]";
  }
}
