// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class AdministrativeStatusTypeResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -8661462528133418593L;

  private AdministrativeStatusTypeDto administrativeStatusTypeDto;

  public AdministrativeStatusTypeResponseDto(
      final AdministrativeStatusTypeDto administrativeStatusTypeDto) {
    this.administrativeStatusTypeDto = administrativeStatusTypeDto;
  }

  public AdministrativeStatusTypeDto getAdministrativeStatusTypeDto() {
    return this.administrativeStatusTypeDto;
  }

  public void setAdministrativeStatusTypeDto(
      final AdministrativeStatusTypeDto administrativeStatusTypeDto) {
    this.administrativeStatusTypeDto = administrativeStatusTypeDto;
  }
}
