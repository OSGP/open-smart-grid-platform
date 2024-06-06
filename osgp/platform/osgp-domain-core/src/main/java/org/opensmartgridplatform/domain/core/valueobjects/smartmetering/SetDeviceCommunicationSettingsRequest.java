// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SetDeviceCommunicationSettingsRequest implements Serializable {

  private static final long serialVersionUID = 6949847554119609182L;

  private final String deviceIdentification;

  private final SetDeviceCommunicationSettingsData setDeviceCommunicationSettingsData;

  public SetDeviceCommunicationSettingsRequest(
      final String deviceIdentification,
      final SetDeviceCommunicationSettingsData setDeviceCommunicationSettingsData) {
    this.deviceIdentification = deviceIdentification;
    this.setDeviceCommunicationSettingsData = setDeviceCommunicationSettingsData;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SetDeviceCommunicationSettingsData getSetDeviceCommunicationSettingsData() {
    return this.setDeviceCommunicationSettingsData;
  }
}
