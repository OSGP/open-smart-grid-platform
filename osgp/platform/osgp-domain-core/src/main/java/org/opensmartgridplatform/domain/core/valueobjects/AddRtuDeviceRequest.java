/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class AddRtuDeviceRequest implements Serializable {

  private static final long serialVersionUID = -6363279003203263772L;

  private final RtuDevice rtuDevice;
  private final DeviceModel deviceModel;

  public AddRtuDeviceRequest(final RtuDevice rtuDevice, final DeviceModel deviceModel) {
    this.rtuDevice = rtuDevice;
    this.deviceModel = deviceModel;
  }

  public RtuDevice getRtuDevice() {
    return this.rtuDevice;
  }

  public DeviceModel getDeviceModel() {
    return this.deviceModel;
  }
}
