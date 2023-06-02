//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
