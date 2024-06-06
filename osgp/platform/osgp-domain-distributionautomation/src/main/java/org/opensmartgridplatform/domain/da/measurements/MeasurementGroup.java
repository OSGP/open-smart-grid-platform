// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeasurementGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  private String identification;
  private List<Measurement> measurements;

  public MeasurementGroup(final String identification, final List<Measurement> measurements) {
    this.identification = identification;
    this.measurements = new ArrayList<>(measurements);
  }

  public String getIdentification() {
    return this.identification;
  }

  public List<Measurement> getMeasurements() {
    return this.measurements;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof MeasurementGroup)) {
      return false;
    }

    final MeasurementGroup that = (MeasurementGroup) obj;
    return Objects.equals(this.identification, that.identification)
        && Objects.equals(this.measurements, that.measurements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.identification, this.measurements);
  }

  @Override
  public String toString() {
    return "MeasurementGroup [identification="
        + this.identification
        + ", measurements="
        + this.measurements
        + "]";
  }
}
