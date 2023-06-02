//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SetConfigurationObjectRequest implements Serializable {

  private static final long serialVersionUID = -8295596279285780413L;

  private final String deviceIdentification;

  private final SetConfigurationObjectRequestData setConfigurationObjectRequestData;

  public SetConfigurationObjectRequest(
      final String deviceIdentification,
      final SetConfigurationObjectRequestData setConfigurationObjectRequestData) {
    this.deviceIdentification = deviceIdentification;
    this.setConfigurationObjectRequestData = setConfigurationObjectRequestData;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SetConfigurationObjectRequestData getSetConfigurationObjectRequestData() {
    return this.setConfigurationObjectRequestData;
  }
}
