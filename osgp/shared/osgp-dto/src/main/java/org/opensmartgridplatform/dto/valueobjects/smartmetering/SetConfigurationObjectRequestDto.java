/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
