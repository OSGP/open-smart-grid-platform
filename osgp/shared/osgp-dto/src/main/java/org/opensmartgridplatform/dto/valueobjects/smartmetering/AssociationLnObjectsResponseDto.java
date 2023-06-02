//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class AssociationLnObjectsResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -1200919940530914061L;

  private final AssociationLnListTypeDto associationLnList;

  public AssociationLnObjectsResponseDto(final AssociationLnListTypeDto associationLnList) {
    this.associationLnList = associationLnList;
  }

  public AssociationLnListTypeDto getAssociationLnList() {
    return this.associationLnList;
  }
}
