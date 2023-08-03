// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SetPointDto implements Serializable {

  private static final long serialVersionUID = -8242555524743018337L;

  private final int id;
  private final String node;
  private final double value;
  private final ZonedDateTime startTime;
  private final ZonedDateTime endTime;

  public SetPointDto(
      final int id,
      final String node,
      final double value,
      final ZonedDateTime startTime,
      final ZonedDateTime endTime) {
    this.id = id;
    this.node = node;
    this.value = value;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public int getId() {
    return this.id;
  }

  public String getNode() {
    return this.node;
  }

  public double getValue() {
    return this.value;
  }

  public ZonedDateTime getStartTime() {
    return this.startTime;
  }

  public ZonedDateTime getEndTime() {
    return this.endTime;
  }
}
