//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SetAdministrationStateDto implements Serializable {

  private static final long serialVersionUID = 9204064540343962380L;

  private AdministrativeStatusTypeDto status;

  private final String deviceIdentification;

  public SetAdministrationStateDto(
      final AdministrativeStatusTypeDto status, final String deviceIdentification) {
    this.status = status;
    this.deviceIdentification = deviceIdentification;
  }

  public AdministrativeStatusTypeDto getStatus() {
    return this.status;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
