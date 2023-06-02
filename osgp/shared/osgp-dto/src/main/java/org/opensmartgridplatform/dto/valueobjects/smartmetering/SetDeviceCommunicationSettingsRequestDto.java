//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SetDeviceCommunicationSettingsRequestDto implements Serializable {

  private static final long serialVersionUID = 3587059321405466449L;

  private final String deviceIdentification;

  private final SetDeviceCommunicationSettingsRequestDataDto setDeviceCommunicationSettingsData;

  public SetDeviceCommunicationSettingsRequestDto(
      final String deviceIdentification,
      final SetDeviceCommunicationSettingsRequestDataDto setDeviceCommunicationSettingsData) {
    this.deviceIdentification = deviceIdentification;
    this.setDeviceCommunicationSettingsData = setDeviceCommunicationSettingsData;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SetDeviceCommunicationSettingsRequestDataDto getSetDeviceCommunicationSettingsData() {
    return this.setDeviceCommunicationSettingsData;
  }
}
