/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CaptureObject implements Serializable {

  private static final long serialVersionUID = 991045734132231909L;

  private final long classId;
  private final String logicalName;
  private final int attributeIndex;
  private final long dataIndex;
  private final String unit;

  public CaptureObject(
      final long classId,
      final String logicalName,
      final int attributeIndex,
      final long dataIndex,
      final String unit) {
    this.classId = classId;
    this.logicalName = logicalName;
    this.attributeIndex = attributeIndex;
    this.dataIndex = dataIndex;
    this.unit = unit;
  }

  public long getClassId() {
    return this.classId;
  }

  public String getLogicalName() {
    return this.logicalName;
  }

  public int getAttributeIndex() {
    return this.attributeIndex;
  }

  public long getDataIndex() {
    return this.dataIndex;
  }

  public String getUnit() {
    return this.unit;
  }
}
