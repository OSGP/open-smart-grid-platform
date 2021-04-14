/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
