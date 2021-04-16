/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
