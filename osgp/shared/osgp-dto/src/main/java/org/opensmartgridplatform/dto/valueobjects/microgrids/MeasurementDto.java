// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class MeasurementDto extends MeasurementIdentifierDto implements Serializable {

  private static final long serialVersionUID = -6999340558343190220L;

  private final int qualifier;
  private final ZonedDateTime time;
  private final double value;

  public MeasurementDto(
      final int id,
      final String node,
      final int qualifier,
      final ZonedDateTime time,
      final double value) {
    super(id, node);
    this.qualifier = qualifier;
    this.time = time;
    this.value = value;
  }

  public int getQualifier() {
    return this.qualifier;
  }

  public ZonedDateTime getTime() {
    return this.time;
  }

  public double getValue() {
    return this.value;
  }
}
