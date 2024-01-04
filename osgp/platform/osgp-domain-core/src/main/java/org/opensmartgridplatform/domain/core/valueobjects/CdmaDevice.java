// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.Objects;

public class CdmaDevice {

  private final String deviceIdentification;
  private final String networkAddress;
  private final String mastSegmentName;
  private final Short batchNumber;

  public CdmaDevice(
      final String deviceIdentification,
      final String networkAddress,
      final String mastSegmentName,
      final Short batchNumber) {
    this.deviceIdentification = deviceIdentification;
    this.networkAddress = networkAddress;
    this.mastSegmentName = mastSegmentName;
    this.batchNumber = batchNumber;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getNetworkAddress() {
    return this.networkAddress;
  }

  public String getMastSegmentName() {
    return this.mastSegmentName;
  }

  public Short getBatchNumber() {
    return this.batchNumber;
  }

  @Override
  public int hashCode() {
    return this.deviceIdentification.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof CdmaDevice)) {
      return false;
    }

    final CdmaDevice other = (CdmaDevice) obj;

    return Objects.equals(this.deviceIdentification, other.deviceIdentification);
  }

  @Override
  public String toString() {
    return "CdmaBatchDevice [deviceIdentification="
        + this.deviceIdentification
        + ", networkAddress="
        + this.networkAddress
        + ", mastSegmentName="
        + this.mastSegmentName
        + ", batchNumber="
        + this.batchNumber
        + "]";
  }
}
