//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.net.InetAddress;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaBatchDevice {

  private String deviceIdentification;
  private InetAddress networkAddress;

  public CdmaBatchDevice(final String deviceIdentification, final InetAddress networkAddress) {
    this.deviceIdentification = deviceIdentification;
    this.networkAddress = networkAddress;
  }

  public CdmaBatchDevice(final CdmaDevice cdmaDevice) {
    this.deviceIdentification = cdmaDevice.getDeviceIdentification();
    this.networkAddress = cdmaDevice.getNetworkAddress();
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public InetAddress getInetAddress() {
    return this.networkAddress;
  }

  @Override
  public String toString() {
    return "CdmaBatchDevice [deviceIdentification="
        + this.deviceIdentification
        + ", networkAddress="
        + this.networkAddress
        + "]";
  }
}
