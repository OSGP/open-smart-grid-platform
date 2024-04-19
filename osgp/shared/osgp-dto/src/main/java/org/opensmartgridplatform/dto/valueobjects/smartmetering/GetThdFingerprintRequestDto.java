// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class GetThdFingerprintRequestDto implements Serializable {

  private static final long serialVersionUID = -7132887300427005193L;

  private final String deviceIdentification;

  public GetThdFingerprintRequestDto(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
