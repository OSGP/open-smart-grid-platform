/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
