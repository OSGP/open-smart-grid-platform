// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;
import org.joda.time.DateTime;

public class SetPoint implements Serializable {

  private static final long serialVersionUID = -8781688280636819412L;

  private final int id;
  private final String node;
  private final double value;
  private final DateTime startTime;
  private final DateTime endTime;

  public SetPoint(
      final int id,
      final String node,
      final double value,
      final DateTime startTime,
      final DateTime endTime) {
    super();
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

  public DateTime getStartTime() {
    return this.startTime;
  }

  public DateTime getEndTime() {
    return this.endTime;
  }
}
