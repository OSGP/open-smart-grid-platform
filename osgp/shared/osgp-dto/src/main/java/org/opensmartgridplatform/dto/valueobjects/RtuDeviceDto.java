//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class RtuDeviceDto implements Serializable {

  private static final long serialVersionUID = 1898203248997085588L;

  protected String deviceIdentification;
  protected String protocolName;
  protected String protocolVersion;
  protected String networkAddress;

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getProtocolName() {
    return this.protocolName;
  }

  public void setProtocolName(final String protocolName) {
    this.protocolName = protocolName;
  }

  public String getProtocolVersion() {
    return this.protocolVersion;
  }

  public void setProtocolVersion(final String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public String getNetworkAddress() {
    return this.networkAddress;
  }

  public void setNetworkAddress(final String networkAddress) {
    this.networkAddress = networkAddress;
  }
}
