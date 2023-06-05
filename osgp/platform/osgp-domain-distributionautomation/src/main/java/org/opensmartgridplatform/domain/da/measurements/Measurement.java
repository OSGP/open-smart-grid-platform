// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Measurement implements Serializable {

  private static final long serialVersionUID = 1L;

  private final ArrayList<MeasurementElement> measurementElements;

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
