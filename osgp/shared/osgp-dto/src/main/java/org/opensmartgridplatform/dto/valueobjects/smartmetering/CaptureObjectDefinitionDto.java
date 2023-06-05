// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class CaptureObjectDefinitionDto implements Serializable {

  private static final long serialVersionUID = -6482013453591999649L;

  private final int classId;
  private final ObisCodeValuesDto logicalName;
  private final byte attributeIndex;
  private final Integer dataIndex;

  public CaptureObjectDefinitionDto(
      final int classId,
      final ObisCodeValuesDto logicalName,
      final byte attributeIndex,
      final Integer dataIndex) {
    this.classId = classId;
    this.logicalName = logicalName;
    this.attributeIndex = attributeIndex;
    this.dataIndex = dataIndex;
  }

  @Override
  public String toString() {
    return String.format(
        "{ %d, %s, %d, %d }",
        this.classId,
        this.logicalName,
        this.attributeIndex,
        this.dataIndex == null ? 0 : this.dataIndex);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CaptureObjectDefinitionDto)) {
      return false;
    }
    final CaptureObjectDefinitionDto other = (CaptureObjectDefinitionDto) obj;
    return this.classId == other.classId
        && Objects.equals(this.logicalName, other.logicalName)
        && this.attributeIndex == other.attributeIndex
        && Objects.equals(this.dataIndex, other.dataIndex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.classId, this.logicalName, this.attributeIndex, this.dataIndex);
  }

  public int getClassId() {
    return this.classId;
  }

  public ObisCodeValuesDto getLogicalName() {
    return this.logicalName;
  }

  public byte getAttributeIndex() {
    return this.attributeIndex;
  }

  public Integer getDataIndex() {
    return this.dataIndex;
  }
}
