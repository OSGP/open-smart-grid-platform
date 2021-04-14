/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.valueobjects.iec61850;

import java.io.Serializable;
import java.util.List;

public class LogicalNode implements Serializable {
  private static final long serialVersionUID = 4776483459295843436L;

  private final String name;
  private List<DataSample> data;

  public LogicalNode(final String name, final List<DataSample> data) {
    this.name = name;
    this.data = data;
  }

  public String getName() {
    return this.name;
  }

  public List<DataSample> getData() {
    return this.data;
  }
}
