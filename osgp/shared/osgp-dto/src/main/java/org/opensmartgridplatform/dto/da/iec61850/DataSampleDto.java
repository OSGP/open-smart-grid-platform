/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.da.iec61850;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DataSampleDto implements Serializable {
  private static final long serialVersionUID = -5737102492719159340L;

  private final String sampleType;
  private final Date timestamp;
  private final BigDecimal value;

  public DataSampleDto(final String sampleType, final Date timestamp, final BigDecimal value) {
    this.sampleType = sampleType;
    this.timestamp = timestamp;
    this.value = value;
  }

  public String getSampleType() {
    return this.sampleType;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public BigDecimal getValue() {
    return this.value;
  }
}
