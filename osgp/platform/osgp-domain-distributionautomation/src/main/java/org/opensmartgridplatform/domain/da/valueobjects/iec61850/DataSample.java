/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.valueobjects.iec61850;

import java.io.Serializable;
import java.util.Date;

public class DataSample implements Serializable {
  private static final long serialVersionUID = 4776483459295843436L;

  private String sampleType;
  private Date timestamp;
  private Double value;

  public DataSample(final String sampleType, final Date timestamp, final Double value) {

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

  public Double getValue() {
    return this.value;
  }
}
