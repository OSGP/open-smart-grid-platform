// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
