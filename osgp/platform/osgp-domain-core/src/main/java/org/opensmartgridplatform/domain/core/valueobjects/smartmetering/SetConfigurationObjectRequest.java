/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
