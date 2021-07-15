/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class AdministrativeStateData implements Serializable {

  private static final long serialVersionUID = -1399391398920839144L;

  private final AdministrativeStatusType status;

  public AdministrativeStateData(final AdministrativeStatusType status) {
    this.status = status;
  }

  public AdministrativeStatusType getAdministrationStateData() {
    return this.status;
  }
}
