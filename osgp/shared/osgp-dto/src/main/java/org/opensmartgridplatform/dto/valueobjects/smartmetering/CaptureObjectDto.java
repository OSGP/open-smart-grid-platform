//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class CaptureObjectDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369208L;

  private final long classId;
  private final String logicalName;
  private final long attributeIndex;
  private final int dataIndex;
  private final String unit;

  public CaptureObjectDto(
      final long classId,
      final String logicalName,
      final long attributeIndex,
      final int dataIndex,
      final String unit) {
    super();
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

  public long getAttributeIndex() {
    return this.attributeIndex;
  }

  public int getDataIndex() {
    return this.dataIndex;
  }

  public String getUnit() {
    return this.unit;
  }
}
