/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import org.joda.time.DateTime;

public class MeasurementDto extends MeasurementIdentifierDto implements Serializable {

  private static final long serialVersionUID = -6999340558343190220L;

  private int qualifier;
  private DateTime time;
  private double value;

  public MeasurementDto(
      final int id,
      final String node,
      final int qualifier,
      final DateTime time,
      final double value) {
    super(id, node);
    this.qualifier = qualifier;
    this.time = time;
    this.value = value;
  }

  public int getQualifier() {
    return this.qualifier;
  }

  public DateTime getTime() {
    return this.time;
  }

  public double getValue() {
    return this.value;
  }
}
