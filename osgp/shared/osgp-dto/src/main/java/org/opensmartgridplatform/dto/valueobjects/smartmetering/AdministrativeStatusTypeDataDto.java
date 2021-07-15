/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
