// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class LightSensorStatusDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private final LightSensorStatusTypeDto status;

  public LightSensorStatusDto(final LightSensorStatusTypeDto status) {
    this.status = status;
  }

  public LightSensorStatusTypeDto getStatus() {
    return this.status;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LightSensorStatusDto)) {
      return false;
    }
    final LightSensorStatusDto other = (LightSensorStatusDto) obj;
    return this.status == other.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.status);
  }

  @Override
  public String toString() {
    return "LightSensorStatusDto [status=" + this.status + "]";
  }
}
