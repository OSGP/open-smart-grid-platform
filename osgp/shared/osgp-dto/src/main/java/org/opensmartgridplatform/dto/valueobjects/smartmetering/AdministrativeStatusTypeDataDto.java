//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class AdministrativeStatusTypeDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -5211585074109860057L;

  private AdministrativeStatusTypeDto administrativeStatusType;

  public AdministrativeStatusTypeDataDto(
      final AdministrativeStatusTypeDto administrativeStatusType) {
    this.administrativeStatusType = administrativeStatusType;
  }

  public AdministrativeStatusTypeDto getAdministrativeStatusType() {
    return this.administrativeStatusType;
  }
}
