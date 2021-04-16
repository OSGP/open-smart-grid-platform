/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public class AdministrativeStatusTypeResponse extends ActionResponse {

  private static final long serialVersionUID = -8661462528133418593L;

  private AdministrativeStatusType administrativeStatusType;

  public AdministrativeStatusTypeResponse(final AdministrativeStatusType administrativeStatusType) {
    this.administrativeStatusType = administrativeStatusType;
  }

  public AdministrativeStatusType getAdministrativeStatusType() {
    return this.administrativeStatusType;
  }

  public void setAdministrativeStatusType(final AdministrativeStatusType administrativeStatusType) {
    this.administrativeStatusType = administrativeStatusType;
  }
}
