//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;

public class DeviceConnectionParameters {

  final String ipAddress;
  final String deviceIdentification;
  final IED ied;
  final String serverName;
  final String logicalDevice;

  public DeviceConnectionParameters(final Builder builder) {
    this.ipAddress = builder.ipAddress;
    this.deviceIdentification = builder.deviceIdentification;
    this.ied = builder.ied;
    this.serverName = builder.serverName;
    this.logicalDevice = builder.logicalDevice;
  }

  public static class Builder {
    private String ipAddress = null;
    private String deviceIdentification = null;
    private IED ied = null;
    private String serverName = null;
    private String logicalDevice = null;

    public Builder ipAddress(final String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Builder deviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder ied(final IED ied) {
      this.ied = ied;
      return this;
    }

    public Builder serverName(final String serverName) {
      this.serverName = serverName;
      return this;
    }

    public Builder logicalDevice(final String logicalDevice) {
      this.logicalDevice = logicalDevice;
      return this;
    }

    public DeviceConnectionParameters build() {
      return new DeviceConnectionParameters(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public IED getIed() {
    return this.ied;
  }

  public String getServerName() {
    return this.serverName;
  }

  public String getLogicalDevice() {
    return this.logicalDevice;
  }
}
