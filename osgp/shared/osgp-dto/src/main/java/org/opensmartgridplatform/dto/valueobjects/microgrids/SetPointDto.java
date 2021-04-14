/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import org.joda.time.DateTime;

public class SetPointDto implements Serializable {

  private static final long serialVersionUID = -8242555524743018337L;

  private int id;
  private String node;
  private double value;
  private DateTime startTime;
  private DateTime endTime;

  public SetPointDto(
      final int id,
      final String node,
      final double value,
      final DateTime startTime,
      final DateTime endTime) {
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
