// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
