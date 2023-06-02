//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.da.measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MeasurementGroupDto implements Serializable {

  private static final long serialVersionUID = 3134694499786904192L;

  private final String identification;
  private final List<MeasurementDto> measurements;

  public MeasurementGroupDto(final String identification, final List<MeasurementDto> measurements) {
    this.identification = identification;
    this.measurements = new ArrayList<>(measurements);
  }

  public String getIdentification() {
    return this.identification;
  }

  public List<MeasurementDto> getMeasurements() {
    return Collections.unmodifiableList(this.measurements);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof MeasurementGroupDto)) {
      return false;
    }
    final MeasurementGroupDto that = (MeasurementGroupDto) obj;
    return Objects.equals(this.identification, that.identification)
        && Objects.equals(this.measurements, that.measurements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.identification, this.measurements);
  }

  @Override
  public String toString() {
    return "MeasurementGroupDto [identification="
        + this.identification
        + ", measurements="
        + this.measurements
        + "]";
  }
}
