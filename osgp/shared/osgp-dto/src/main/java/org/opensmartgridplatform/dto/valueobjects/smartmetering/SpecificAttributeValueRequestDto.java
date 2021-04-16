/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SpecificAttributeValueRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 2506458162101143461L;

  private int classId;
  private int attribute;
  private ObisCodeValuesDto obisCode;

  public SpecificAttributeValueRequestDto(
      final int classId, final int attribute, final ObisCodeValuesDto obisCode) {
    super();
    this.classId = classId;
    this.attribute = attribute;
    this.obisCode = obisCode;
  }

  public int getClassId() {
    return this.classId;
  }

  public int getAttribute() {
    return this.attribute;
  }

  public ObisCodeValuesDto getObisCode() {
    return this.obisCode;
  }
}
