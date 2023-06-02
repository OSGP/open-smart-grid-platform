//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
