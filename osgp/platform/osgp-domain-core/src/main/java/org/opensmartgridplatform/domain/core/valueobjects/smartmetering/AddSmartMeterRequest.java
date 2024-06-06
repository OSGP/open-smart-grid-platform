// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;

public class AddSmartMeterRequest implements Serializable {

  private static final long serialVersionUID = -6363279003203263772L;

  final SmartMeteringDevice device;

  final DeviceModel deviceModel;

  final Boolean overwrite;

  public AddSmartMeterRequest(
      final SmartMeteringDevice device, final DeviceModel deviceModel, final Boolean overwrite) {
    this.device = device;
    this.deviceModel = deviceModel;
    this.overwrite = overwrite;
  }

  public AddSmartMeterRequest(final SmartMeteringDevice device, final DeviceModel deviceModel) {
    this.device = device;
    this.deviceModel = deviceModel;
    this.overwrite = false;
  }

  public SmartMeteringDevice getDevice() {
    return this.device;
  }

  public DeviceModel getDeviceModel() {
    return this.deviceModel;
  }

  public Boolean getOverwrite() {
    return this.overwrite;
  }
}
