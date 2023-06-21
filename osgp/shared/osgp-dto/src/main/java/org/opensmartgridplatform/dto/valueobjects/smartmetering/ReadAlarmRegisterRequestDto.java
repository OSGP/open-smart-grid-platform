// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ReadAlarmRegisterRequestDto implements Serializable {
  private static final long serialVersionUID = 3751586818507193990L;

  private String deviceIdentification;

  public ReadAlarmRegisterRequestDto(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
