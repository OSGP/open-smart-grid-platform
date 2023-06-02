//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SetConfigurationObjectRequestDto implements Serializable {

  private static final long serialVersionUID = -8295596279285780413L;

  private final String deviceIdentification;

  private final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestData;

  public SetConfigurationObjectRequestDto(
      final String deviceIdentification,
      final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestData) {
    this.deviceIdentification = deviceIdentification;
    this.setConfigurationObjectRequestData = setConfigurationObjectRequestData;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SetConfigurationObjectRequestDataDto getSetConfigurationObjectRequestData() {
    return this.setConfigurationObjectRequestData;
  }
}
