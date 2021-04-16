/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class CosemObjectDefinitionDto implements Serializable {

  private static final long serialVersionUID = -4295400968186675563L;

  private final int classId;
  private final CosemObisCodeDto logicalName;
  private final int attributeIndex;
  private final int dataIndex;

  public CosemObjectDefinitionDto(
      final int classId,
      final CosemObisCodeDto logicalName,
      final int attributeIndex,
      final int dataIndex) {
    this.checkFields(classId, logicalName, attributeIndex, dataIndex);
    this.classId = classId;
    this.logicalName = logicalName;
    this.attributeIndex = attributeIndex & 0xFF;
    this.dataIndex = dataIndex;
  }

  public CosemObjectDefinitionDto(
      final int classId, final CosemObisCodeDto logicalName, final int attributeIndex) {
    this(classId, logicalName, attributeIndex, 0);
  }

  private void checkFields(
      final int classId,
      final CosemObisCodeDto logicalName,
      final int attributeIndex,
      final int dataIndex) {
    this.checkClassId(classId);
    Objects.requireNonNull(logicalName, "logicalName must not be null");
    this.checkAttributeIndex(attributeIndex);
    this.checkDataIndex(dataIndex);
  }

  private void checkClassId(final int classId) {
    if (classId < 0 || classId > 0xFFFF) {
      throw new IllegalArgumentException("classId not in [0..65535]: " + classId);
    }
  }

  private void checkAttributeIndex(final int attributeIndex) {
    if (attributeIndex < -128 || attributeIndex > 255) {
      throw new IllegalArgumentException(
          "attributeIndex not in byte value range: " + attributeIndex);
    }
  }

  private void checkDataIndex(final int dataIndex) {
    if (dataIndex < 0 || dataIndex > 0xFFFF) {
      throw new IllegalArgumentException("dataIndex not in [0..65535]: " + dataIndex);
    }
  }

  @Override
  public String toString() {
    return String.format(
        "CosemObjectDefinition[%d, %s, %d, %d]",
        this.classId, this.logicalName, this.attributeIndex, this.dataIndex);
  }

  public String toDsmrString() {
    return String.format(
        "{%d,%s,%d,%d}",
        this.classId, this.logicalName.toDsmrString(), this.attributeIndex, this.dataIndex);
  }

  public String toHexString() {
    return String.format(
        "%04X%s%02X%04X",
        this.classId, this.logicalName.toHexString(), this.attributeIndex, this.dataIndex);
  }

  public int getClassId() {
    return this.classId;
  }

  public CosemObisCodeDto getLogicalName() {
    return this.logicalName;
  }

  public int getAttributeIndex() {
    return this.attributeIndex;
  }

  public int getDataIndex() {
    return this.dataIndex;
  }
}
